function addBankHoliday() {
    let date_input = $('input[name="date"]');
    let dateText = date_input.val();
    if (dateText !== "") {
        const parts = dateText.split('/');
        const date = {
            'year': parts[2],
            'month': parts[1],
            'day': parts[0]
        }
        _addBankHolidayToDatabase(date);
    }
    let myModal = $('#addBankHoliday');
    myModal.modal('hide');
}

function _addBankHolidayToDatabase(date) {
    $.ajax({
        type: "POST",
        url: "/api/bankHolidays/post",
        data: "json=" + JSON.stringify(date),
    });
}

function goToEmployeeManager() {
    window.location.replace("/admin/employees");
}

function addProject() {
    let somethingWrong = false;

    let abbr = $('#project_abbr').val();
    if (abbr === '' || abbr === null) somethingWrong = true;

    let name = $('#project_name').val();
    if (name === '' || name === null) somethingWrong = true;

    let fullName = $('#project_fullName').val();
    if (fullName === '' || fullName === null) somethingWrong = true;

    let company = $('#project_company').val();
    if (company === '' || company === null) somethingWrong = true;

    if (!somethingWrong) {

        let projectData = {
            'abbr': abbr,
            'name': name,
            'fullName': fullName,
            'companyName': company
        }

        _addProject(projectData);

        let myModal = $('#addProjectModal');
        myModal.modal('hide');
    }
}

function _addProject(data) {
    $.ajax({
        type: "POST",
        url: "/api/project/add",
        data: "json=" + JSON.stringify(data)
    });
}

function loadExcelModal() {
    let periodSelect = $('#export_periodSelect');

    $.ajax({
        type: "GET",
        url: "/api/periods/get",
        success: (response) => {
            periodSelect.html('');
            for(let item of response) {
                let option = document.createElement('option');
                option.value = item.id;
                option.text = item.name;
                $(option).appendTo(periodSelect);
            }
        }
    });


    let myModal = $('#exportExcelModal');
    myModal.modal('show');
}

function downloadExcel() {
    let selectedPeriodId = $('#export_periodSelect option:selected').val();

    window.location.replace(`/api/excel/get/${selectedPeriodId}`);

    // $.ajax({
    //     type: "GET",
    //     url: `/api/excel/get/${selectedPeriodId}`,
    // });

    // let myModal = $('#exportExcelModal');
    // myModal.modal('hide');
}