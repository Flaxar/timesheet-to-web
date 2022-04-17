let modifyLogButton;
let deleteLogButton;
let duplicateButton;

let employeeName;
let employeeId;
let employeeWorkHours;
let currentMonthWorkDays;

let projects = [];

let rowData = [];

let currentlyEditedRow; // TODO get rid of this global variable

let hoursWorkedThisMonth;

let tbody;

let periodId;
let periodName;

let editActivityAction; // TODO get rid of this global variable and replace it with a form field in the modal

$(document).ready(() => {
    modifyLogButton = document.getElementById('modifyButton');
    deleteLogButton = document.getElementById('deleteButton');
    duplicateButton = document.getElementById('duplicateButton');
    tbody = document.getElementById('logTable').getElementsByTagName('tbody')[0];

    _fetchEmployeeInfo();
    _fetchPeriodInfo(); // fetches the rest of period-dependent data too

    _fetchProjects();
    _fetchPeriods();

    let date_input = $('input[name="date"]'); //our date input has the name "date"
    let container = $('.bootstrap-iso form').length > 0 ? $('.bootstrap-iso form').parent() : "body";
    date_input.datepicker({
        format: 'dd/mm/yyyy',
        container: container,
        todayHighlight: true,
        autoclose: true,
        language: 'cs'
    })

    $('#editActivityModal').on('show.bs.modal', function(e) {
        editActivityAction = $(e.relatedTarget).attr('data-form-action'); // TODO get rid of the global value, let this be a form field in the modal

        let modal = $('#editActivityModal');
        modal.find(".action-title").hide();
        modal.find(".action-title-" + editActivityAction).show(); // action-title-EDIT, action-title-CREATE action-title-COPY

        if (editActivityAction == 'CREATE') {
            activityEraseValues();
        };
    });

    $("#editActivityModal").find('form').on('submit', function(e) {
        e.preventDefault(); // important, to prevent standard submit via form action="..."
        editActivitySave();
    });


    $('#switch_period_id').on('change', function() {
        // console.log('period has changed');
        periodId = $('#switch_period_id').val();
        tbody.innerHTML = ''; // TODO replace with some loader image
        _fetchPeriodInfo();
    });
});

function _fetchEmployeeInfo() {
    fetch('/api/employee_info/get').then((response) => {
        response.json().then(data => {
            employeeName = data.name;
            employeeId = data.id;
            employeeWorkHours = parseInt(data.workHoursPer100Days) / 100;
            $('#header_name').text(data.name);
            _setStats();
        });
    }).catch(() => {
        console.log("Error with fetching data");
    });
}

function _fetchPeriodInfo() {
    let url;
    if (periodId == null) {
        url = '/api/period_info/get/default';
    } else {
        url = '/api/period_info/get/' + periodId;
    }
    $.ajax({
        type: "GET",
        url: url,
        success: function(response) {
            periodId = response.id;
            periodName = response.name;
            _afterPeriodUpdated();
        }
    });
}

function _afterPeriodUpdated() {
    _fetchWorkDays();
    _fetchLogTable();
}

function _fetchWorkDays() {
    $.ajax({
        type: "GET",
        url: "/api/work_days/get",
        data: {
            "periodId": periodId
        },
        success: function(response) {
            currentMonthWorkDays = parseInt(response);
            _setStats();
        }
    });
}

function _fetchLogTable() {
    $(tbody).html(''); // FIXME is this still needed? Or replace it with some loader image
    $.ajax({
        type: "GET",
        url: "/api/log/get/" + periodId,
        success: function(response) {
            hoursWorkedThisMonth = 0.0;

            rowData = [];
            for (const item of response) {
                _generateNewTableRow(item);
            }

            _setStats();
        }
    });
}

function _setStats() {
    if (currentMonthWorkDays === undefined || employeeWorkHours === undefined || hoursWorkedThisMonth === undefined) {
        $('#header_hoursRatio').text('');
        $('#header_hoursDiff').text('');
        return;
    }
    let tmp = currentMonthWorkDays * employeeWorkHours;
    let text = `${hoursWorkedThisMonth.toString()}h / ${tmp.toString()}h`
    $('#header_hoursRatio').text(text);

    tmp = (currentMonthWorkDays * employeeWorkHours) - hoursWorkedThisMonth;
    if (tmp < 0) tmp = 0;
    $('#header_hoursDiff').text(`Zbývá ${tmp}h`);
}

// loads projects from API and uses them to create HTML <select> <option> data
function _fetchProjects() {
    let projectSelect = $('#edit_projectSelect');
    //console.debug("filling project select with options 1");
    $.ajax({
        type: "GET",
        url: "api/projects/get",
        success: function(response) {
            projectSelect.html('<option></option>');
            projects = [];
            for (const item of response) {
                let option = document.createElement('option');
                option.value = item.id;
                option.text = item.name;
                $(option).appendTo(projectSelect);
                projects.push(item);
            }
        }
    });
}

function _fetchPeriods() {
    let periodSelect = $('#switch_period_id');
    let periodEditSelect = $('#edit_periodId');
    let selectList = periodSelect.add(periodEditSelect);

    $.ajax({
        type: "GET",
        url: "api/periods/get",
        success: function(response) {
            periodSelect.html('');
            periodEditSelect.html(''); // always pre-filled         // '<option value=""></option>';
            for (const item of response) {
                let option = document.createElement('option');
                option.value = item.id;
                option.text = item.name;
                $(option).appendTo(selectList);
            }
        }
    });
}

function _getSingleRowDataById(id) {
    for (const row of rowData) {
        if (row.id === id) return row;
    }
}

function _setSingleRowDataById(id, data) {
    for (let row of rowData) {
        if (row.id === id) {
            row = data;
        }
    }
}


function _generateNewTableRow(data) {

    rowData.push(data);

    let newRow = tbody.insertRow();
    newRow.dataset.id = data.id;
    newRow.dataset.month = data.month;
    //newRow.dataset.year = data.year;
    newRow.dataset.name = data.employeeName;
    newRow.dataset.projectId = data.projectId;

    _createTableTextCell(newRow, 'EP Brno Project, s.r.o.'); // FIXME shouldn't this be in a database too? Linked to the project?
    _createTableTextCell(newRow, data.projectName);
    _createTableTextCell(newRow, data.companyName);
    _createTableTextCell(newRow, data.service);
    _createTableTextCell(newRow, data.details === null ? '' : data.details);
    _createTableTextCell(newRow, data.hoursWorkedTimes1000 / 1000);
    hoursWorkedThisMonth += data.hoursWorkedTimes1000 / 1000;

    let newCell = newRow.insertCell();
    const newRowIndex = newRow.rowIndex;
    let newModifyButton = modifyLogButton.cloneNode(true);
    let newDeleteButton = deleteLogButton.cloneNode(true);
    let newDuplicateButton = duplicateButton.cloneNode(true);

    newModifyButton.dataset.rowId = data.id;
    newModifyButton.dataset.rowIndex = newRowIndex.toString();
    newModifyButton.hidden = false;
    newModifyButton.style.marginRight = '10px';

    newDeleteButton.dataset.rowId = data.id;
    newDeleteButton.dataset.rowIndex = newRowIndex.toString();
    newDeleteButton.hidden = false;
    newDeleteButton.style.marginRight = '10px';

    newDuplicateButton.dataset.rowId = data.id;
    newDuplicateButton.dataset.rowIndex = newRowIndex.toString();
    newDuplicateButton.hidden = false;
    newDuplicateButton.style.marginRight = '10px';

    newCell.appendChild(newModifyButton);
    newCell.appendChild(newDeleteButton);
    newCell.appendChild(newDuplicateButton);
}

function _createTableTextCell(row, text) {
    let newCell = row.insertCell();
    let cellText = document.createTextNode(text);
    newCell.appendChild(cellText);
}

function activityEraseValues() {
    $('#edit_projectSelect').val('');
    $('#edit_serviceInput').val('');
    $('#edit_serviceDetailInput').val('');
    $('#edit_hoursWorkedInput').val('');
    $('#edit_periodId').val(periodId);
}

function activityPrefillValues(rowData) {
    $('#edit_projectSelect').val(rowData.projectId);
    $('#edit_serviceInput').val(rowData.service);
    $('#edit_serviceDetailInput').val(rowData.details);
    $('#edit_hoursWorkedInput').val(rowData.hoursWorkedTimes1000 / 1000);
    $('#edit_periodId').val(rowData.periodId);
}

function showEditRow(callingButton) { // FIXME generic name but the function is specific to Actions
    let rowId = parseInt(callingButton.dataset.rowId);

    let rows = tbody.rows;
    currentlyEditedRow = rows[callingButton.dataset.rowIndex - 1];

    activityPrefillValues(_getSingleRowDataById(rowId));
    $('#edit_activityId').val(rowId);
    let myModal = $('#editActivityModal');
    myModal.modal('show', callingButton);
}

function showDuplicateRow(callingButton) { // FIXME generic name but the function is specific to Actions
    let rowId = parseInt(callingButton.dataset.rowId);

    activityPrefillValues(_getSingleRowDataById(rowId));
    $('#edit_activityId').val('');
    $('#edit_periodId').val(periodId);
    let myModal = $('#editActivityModal');
    myModal.modal('show', callingButton);
}

function editActivitySave() {
    let selectedProjectId = $('#edit_projectSelect option:selected').val();

    //console.debug('projects=%o', projects);
    let project = null;
    //console.log(projects);
    for (project of projects) {
        if (parseInt(project.id) === parseInt(selectedProjectId)) {
            break;
        }
    }

    let service = $('#edit_serviceInput').val();
    let serviceDetail = $('#edit_serviceDetailInput').val();
    let hoursWorkedText = $('#edit_hoursWorkedInput').val();
    hoursWorkedText = hoursWorkedText.replaceAll(',', '.');
    let hoursWorkedDec = parseFloat(hoursWorkedText);
    let periodId = $('#edit_periodId').val();

    let data = {
        'employeeName': employeeName,
        'employeeId': employeeId,
        'projectName': project.name,
        'projectId': project.id,
        'companyName': project.companyName,
        'periodId': periodId,
        'service': service,
        'details': serviceDetail,
        'hoursWorkedTimes1000': hoursWorkedDec * 1000
    };

    let api_url;
    if (editActivityAction == 'EDIT') {
        api_url = "/api/log/update";
        data.id = $('#edit_activityId').val();
    } else {
        api_url = "/api/log/create";
    };

    //console.log('data to send=%o', data);
    $.ajax({
            type: "POST",
            url: api_url,
            contentType: 'application/json',
            data: JSON.stringify(data),
        })
        .done(function(data, textStatus, jqXHR) {
            //console.log('done, %o, %o, %o', data, textStatus, jqXHR);
            _fetchLogTable();
            _setStats();
            let myModal = $('#editActivityModal');
            myModal.modal('hide');
            editActivityAction = ''; // to be explicitly set next time to a specific action at the time the modal is displayed
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            //console.log('fail, %o, %o, %o', jqXHR, textStatus, errorThrown);
            alert('Chyba při ukládání údajů.'); // textStatus);
        });
}

function showDeleteRow(callingButton) { // FIXME collision with the same function in employeeManager.html, // FIXME generic name but the function is specific to Actions
    let rows = tbody.rows;
    currentlyEditedRow = rows[callingButton.dataset.rowIndex - 1];

    $('#delete_activityId').val(parseInt(callingButton.dataset.rowId));
    let myModal = $('#deleteActivityModal');
    myModal.modal('show');
}

function deleteRow() { // FIXME collision with the same function in employeeManager.html, // FIXME generic name but the function is specific to Actions
    let rows = tbody.rows;
    currentlyEditedRow.outerHTML = '';

    _deleteDatabaseData($('#delete_activityId').val());

    let myModal = $('#deleteActivityModal');
    myModal.modal('hide');
}

function _deleteDatabaseData(id) {
    $.ajax({
        type: "DELETE",
        url: "/api/log/delete",
        data: "id=" + id,
        success: function() {
            _fetchLogTable();
        }
    });
}