package com.example.timesheet;

import com.example.timesheet.restControllers.*;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.timesheet.AppConfigProperties;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Properties;

public class DatabaseController {
    private static DatabaseController INSTANCE;
    
    /*
    * checks if database already exists, if not, it creates one,
    * and it connects to it
    */
    public DatabaseController() {
        System.out.println("in a DB controller constructor");
        // initConnection();
        // createDatabase();    // Not needed anymore, the database is maintained externally
    }



    public static DatabaseController getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DatabaseController();
        }
        return INSTANCE;
    }


    private InputStream getResourceAsStream(String filename) {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }

    private String readBigStringIn(BufferedReader buffIn) throws IOException {
        StringBuilder everything = new StringBuilder();
        String line;
        while( (line = buffIn.readLine()) != null) {
           everything.append(line);
        }
        return everything.toString();
    }

    private void createDatabaseFromFile(String filename) {
        System.out.println(String.format("Loading SQL create statements from file %s ... ", filename));
        Connection connection;
        try {
            connection = initConnection();
            Statement statement = connection.createStatement();

            BufferedReader br = new BufferedReader(new InputStreamReader(getResourceAsStream(filename)));
            String sql = readBigStringIn(br);
            String[] commands = sql.split(";", 0);

            for (String command : commands) {
                System.out.println(String.format("Executing: %s", command));
                statement.execute(command);
            };

            br.close();
            statement.close();
            connection.close();

            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

    private Connection initConnection() {
        Connection connect;
        Properties info;

        info = new java.util.Properties();
        info.put("user", AppConfigProperties.datasourceUsername);
        info.put("password", AppConfigProperties.datasourcePassword);

        try {
            connect = DriverManager.getConnection(AppConfigProperties.datasourceURL, info);
            // System.out.println("Connected to the database '" + AppConfigProperties.datasourceURL + "' as '" + AppConfigProperties.datasourceUsername + "'.");
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
        };
        return null;
    }

    private void createDatabase() {
        createDatabaseFromFile("database/create.sql");
    }

    public List<Credentials> getCredentials() {
        List<Credentials> credentials = new ArrayList<>();
        try{
            String sql = "SELECT * FROM credentials ORDER BY id DESC";
            Connection connection = initConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while(results.next()) {
                Credentials onePerson = new Credentials(
                        results.getString("username"),
                        results.getString("password"),
                        results.getString("role")
                );
                credentials.add(onePerson);
            }
            results.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    public List<String> getDepartments() {

        List<String> departments = new ArrayList<>();
        try {
            String sql = "SELECT * FROM department ORDER BY name";  // FIXME order by?
            Connection connection = initConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while(results.next()) {
                departments.add(results.getString("name"));
            }
            results.close();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return departments;
    }

    public List<Map.Entry<String, Integer>> getWorkHours() {
        List<HashMap.Entry<String, Integer>> workHours = new ArrayList<>();
        try {
            String sql = "SELECT * FROM workhours ORDER BY id DESC";    // FIXME order by?
            Connection connection = initConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);


            while(results.next()) {
                AbstractMap.Entry<String, Integer> entry =
                        new AbstractMap.SimpleEntry<String, Integer>(
                                results.getString("type"),
                                results.getInt("hours_per_100days")
                        );
                workHours.add(entry);
            }

            results.close();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return workHours;
    }

    public void addDepartments(List<String> newDepartments) {
        try {
            for(String newDepartment : newDepartments) {
                Connection connection = initConnection();
                String sql =
                        "INSERT INTO department (name) SELECT (?)\n" +
                                "WHERE NOT EXISTS (SELECT * FROM department WHERE name = ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, newDepartment);
                statement.setString(2, newDepartment);
                statement.executeUpdate();
                statement.close();
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addWorkHours(List<WorkHours> newWorkHousList) {
        try {
            for(WorkHours newWorkHours : newWorkHousList) {
                Connection connection = initConnection();
                String sql =
                        "INSERT INTO workhours(type, hours_per_100days) SELECT ?, ?\n" +
                                "    WHERE NOT EXISTS (SELECT * FROM workhours WHERE type = ? AND hours_per_100days = ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, newWorkHours.getType());
                statement.setInt(2, newWorkHours.getHours());
                statement.setString(3, newWorkHours.getType());
                statement.setInt(4, newWorkHours.getHours());
                statement.executeUpdate();
                statement.close();
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addNewUser(NewUser newUser) {

        try {

            int credentialsId  = getCredentialsId(newUser.getName(), newUser.getRole());
            int departmentId = getDepartmentId(newUser.getDepartment());
            int workHoursId  = getWorkHoursId(newUser.getWorkHours());

            Connection connection = initConnection();
            String sql = "INSERT INTO employee(name, department_id, work_hours_id, credentials_id) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, newUser.getName());
            statement.setInt(2, departmentId);
            statement.setInt(3, workHoursId);
            statement.setInt(4, credentialsId);

            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private int getDepartmentId(String name) throws SQLException {
        Connection connection = initConnection();
        String sql = "SELECT id FROM department WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet results = statement.executeQuery();
        results.next(); // fetch the row
        int id = results.getInt("id");
        connection.close();
        return id;
    }

    private int getWorkHoursId(WorkHours workHours) throws SQLException {
        Connection connection = initConnection();
        String sql = "SELECT id FROM workhours WHERE type = ? AND hours_per_100days = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, workHours.getType());
        statement.setInt(2, workHours.getHours());
        ResultSet results = statement.executeQuery();
        results.next(); // fetch the row
        int id = results.getInt("id");
        connection.close();
        return id;
    }

    /*
     * Creates new default credentials for the new user that will
     * be changed after their first login.
     * Credentials are created like this:
     *      name: Filip Schwarz -> filip_schwarz1
     *      password: default   (will be changed by the user)
     */
    private int getCredentialsId(String name, String role) throws SQLException {
        int newId;
        
        Connection connection = initConnection();
        try {
            connection.setAutoCommit(false);   // epic fail, can't modify state of a SHARED OBJECT
            String sql = "INSERT INTO credentials(username, password, role) VALUES (\"tmp\", ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, new BCryptPasswordEncoder(10).encode("default"));
            statement.setString(2, role);
            statement.executeUpdate();

            statement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet results = statement.executeQuery();
            results.next(); // fetch the row
            newId = results.getInt(1);

            // username creation
            name = name.toLowerCase();
            name = name.replaceAll(" ", "_");
            name = name + newId;
            GeneralStorage.lastAddedUsername = name;

            sql = "UPDATE credentials SET username = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, newId);
            statement.executeUpdate();
            statement.close();

            if (role.equals("ADMIN")) { // disables admin login
                sql = "UPDATE Credentials SET enabled = 0 WHERE id = 1";
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                statement.close();
            }

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
            newId = 0;  // FIXME or exit?
        }
        connection.close();

        return newId;
    }

    public void updatePassword(String passwordNotEncrypted, String username) {
        try {
            System.out.println("Username: " + username);
            System.out.println("Password: " + passwordNotEncrypted);
            String sql = "UPDATE credentials SET password = ? WHERE username = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, new BCryptPasswordEncoder().encode(passwordNotEncrypted));
            statement.setString(2, username);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    public double getUserWorkHoursPerPeriod(String username) {
        double workHoursPerMonth = 0;

        try {
            String sql = "SELECT workhours.hours_per_100days FROM credentials\n" +
                    "\tINNER JOIN employee ON employee.credentials_id = credentials.id\n" +
                    "\tINNER JOIN workhours ON employee.work_hours_id = workhours.id\n" +
                    "\tWHERE username = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            results.next(); // fetch the row

            double hoursPerDay = results.getInt("hours_per_100days") / 100.0;
            WorkDays workDays = new WorkDays();
            workHoursPerMonth = hoursPerDay * workDays.getNumberOfWorkDaysInPeriod(null);   // FIXME works with the concept of a 'current' period, but the periodId or period should be passed as a parameter here

            statement.close();
            results.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return workHoursPerMonth;
    }

    public double getUserWorkedHours(String username) {
        double workedHours = 0;

        try {
            String sql = "SELECT SUM(log.hours_worked_1000times) FROM credentials\n" +
                    "\tINNER JOIN employee ON employee.credentials_id = credentials.id\n" +
                    "\tINNER JOIN log ON employee.id = log.employee_id\n" +
                    "\tWHERE username = ?";

            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            results.next(); // fetch the row

             workedHours = results.getInt(1) / 1000.0;

            statement.close();
            results.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return workedHours;
    }

    public String getName(String username) {
        String name = "Jmeno se nepovedlo nacist";
        try {
            String sql = "SELECT employee.name FROM credentials\n" +
                    "\tINNER JOIN employee ON employee.credentials_id = credentials.id\n" +
                    "\tWHERE credentials.username = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            results.next(); // fetch the row
            name = results.getString("name");

            results.close();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }

    public List<Log> getLogs(String username, Integer periodId) {
        List<Log> logs = new ArrayList<>();
        try {
            String sql = "SELECT log.id, employee.name, projects.abbr, projects.name, projects.company, log.period_id, periods.name, log.service, log.details, log.hours_worked_1000times, employee.id, projects.id FROM log\n" +
                    "\tINNER JOIN employee ON employee.id = log.employee_id\n" +
                    "\tINNER JOIN projects ON projects.id = log.project_id\n" +
                    "\tINNER JOIN periods ON periods.id = log.period_id\n" +
                    "\tINNER JOIN credentials ON credentials.id = employee.credentials_id\n" +
                    "\tWHERE credentials.username = ? AND log.period_id = ?\n" +
                    "\tORDER BY log.id DESC";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, periodId);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Log log = new Log();

                log.setId(results.getInt(1));
                log.setEmployeeName(results.getString(2));
                log.setProjectName(results.getString(3) + " - " + results.getString(4));
                log.setCompanyName(results.getString(5));
                log.setPeriodId(results.getInt(6));
                log.setPeriodName(results.getString(7));
                log.setService(results.getString(8));
                log.setDetails(results.getString(9));
                log.setHoursWorkedTimes1000(results.getInt(10));
                log.setEmployeeId(results.getInt(11));
                log.setProjectId(results.getInt(12));

                logs.add(log);
            }

            results.close();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return logs;
    }

    public List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        try {
            String sql = "SELECT projects.abbr, projects.name, projects.company, projects.id FROM projects ORDER BY projects.abbr";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery();

            while(results.next()) {
                Project project = new Project();
                project.setName(results.getString(1) + " - " + results.getString(2));
                project.setCompanyName(results.getString(3));
                project.setId(results.getInt(4));
                projects.add(project);
            }

            results.close();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return projects;
    }

    public List<Period> getPeriods() {
        List<Period> periods = new ArrayList<>();
        try {
            String sql = "SELECT periods.id, periods.start, periods.end, periods.name FROM periods WHERE periods.start <= DATE(NOW()) ORDER BY periods.start DESC";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery();

            while(results.next()) {
                Period period = new Period();
                period.setId(results.getInt(1));
                period.setStart(results.getDate(2).toLocalDate());
                period.setEnd(results.getDate(3).toLocalDate());
                period.setName(results.getString(4));
                periods.add(period);
            }

            results.close();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return periods;
    }

    public Period getPeriodInfo(Integer periodId) {
        Period period = new Period();
        try {
            String sql;
            PreparedStatement statement;
            Connection connection = initConnection();
            if (periodId == null) {
                sql = "SELECT periods.id, periods.start, periods.end, periods.name FROM periods WHERE DATE(NOW()) BETWEEN periods.start AND periods.end";
                statement = connection.prepareStatement(sql);
            } else {
                sql = "SELECT periods.id, periods.start, periods.end, periods.name FROM periods WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, periodId);
            };

            ResultSet results = statement.executeQuery();
            results.next();

            period.setId(results.getInt(1));
            period.setStart(results.getDate(2).toLocalDate());
            period.setEnd(results.getDate(3).toLocalDate());
            period.setName(results.getString(4));

            results.close();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return period;
    }

    public Integer addNewLog(Log log) {
        Integer id = -1;
        try {
            String sql = "INSERT INTO log(employee_id, project_id, period_id, hours_worked_1000times, service, details) VALUES\n" +
                    "(?, ?, ?, ?, ?, ?)";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, log.getEmployeeId());
            statement.setInt(2, log.getProjectId());
            statement.setInt(3, log.getPeriodId());
            statement.setInt(4, log.getHoursWorkedTimes1000());
            statement.setString(5, log.getService());
            statement.setString(6, log.getDetails());

            statement.executeUpdate();

            statement.close();

            statement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet results = statement.executeQuery();
            results.next(); // fetch the row
            id = results.getInt(1);

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public Employee getEmployee(String username) {
        Employee employee = new Employee();
        try {
            String sql = "SELECT employee.id, employee.name, workhours.hours_per_100days FROM employee \n" +
                    "\tINNER JOIN credentials ON credentials.id = employee.credentials_id\n" +
                    "INNER JOIN workhours ON workhours.id = employee.work_hours_id\n" +
                    "\tWHERE credentials.username = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();

            while(results.next()) {
                employee.setId(results.getInt(1));
                employee.setName(results.getString(2));
                employee.setWorkHoursPer100Days(results.getInt(3));
            }

            results.close();
            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return employee;
    }


    public Integer updateLog(Log log) {
        Integer id = -1;
        try {
            String sql = "UPDATE log SET project_id = ?, hours_worked_1000times = ?, service = ?, details = ?, period_id = ?\n" +
                    "WHERE id = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, log.getProjectId());
            statement.setInt(2, log.getHoursWorkedTimes1000());
            statement.setString(3, log.getService());
            statement.setString(4, log.getDetails());
            statement.setInt(5, log.getPeriodId());
            statement.setInt(6, log.getId());
            statement.executeUpdate();

            id = log.getId();

            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public void deleteLog(Integer id) {
        try {
            String sql = "DELETE FROM log WHERE id = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isBankHoliday(LocalDate toTest) {
        boolean ret = false;
        try {
            String sql = "SELECT * FROM holidays WHERE year = ? AND month = ? AND day = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, toTest.getYear());
            statement.setInt(2, toTest.getMonthValue());
            statement.setInt(3, toTest.getDayOfMonth());
            ResultSet results = statement.executeQuery();
            ret = results.next();
            statement.close();
            results.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }

    public List<Date> getBankHolidays() {
        List<Date> bankHolidays = new ArrayList<>();
        try {
            String sql = "SELECT * FROM holidays ORDER BY year DESC, month DESC, date DESC";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                Date holiday = new Date();
                holiday.setYear(results.getInt("year"));
                holiday.setMonth(results.getInt("month"));
                holiday.setDay(results.getInt("day"));
                bankHolidays.add(holiday);
            }
            statement.close();
            results.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return bankHolidays;
    }

    public void addBankHoliday(Date bankHoliday) {
        try {
            String sql = "INSERT INTO holidays(year, month, day) VALUES (?, ?, ?)";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, bankHoliday.getYear());
            statement.setInt(2, bankHoliday.getMonth());
            statement.setInt(3, bankHoliday.getDay());
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> allEmployees = new ArrayList<>();
        try {
            String sql = "SELECT employee.id, employee.name, department.name, workhours.type, workhours.hours_per_100days FROM employee \n" +
                    "INNER JOIN department ON department.id = employee.department_id \n" +
                    "INNER JOIN workhours ON workhours.id = employee.work_hours_id \n" +
                    "WHERE active = 1 \n" +
                    " ORDER BY employee.id DESC";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery();

            while(results.next()) {
                Employee employee = new Employee();

                employee.setId(results.getInt(1));
                employee.setName(results.getString(2));
                employee.setDepartment(results.getString(3));
                employee.setWorkHoursType(results.getString(4));
                employee.setWorkHoursPer100Days(results.getInt(5));

                allEmployees.add(employee);
            }

            results.close();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return allEmployees;
    }

    public String addEmployee(Employee employee) {
        WorkHours tmp = new WorkHours();
        tmp.setHours(employee.getWorkHoursPer100Days());
        tmp.setType(employee.getWorkHoursType());
        addWorkHours(Collections.singletonList(tmp));
        addDepartments(Collections.singletonList(employee.getDepartment()));

        NewUser newUser = new NewUser();
        newUser.setName(employee.getName());
        newUser.setDepartment(employee.getDepartment());
        newUser.setWorkHours(tmp);
        newUser.setRole(employee.getRole());

        addNewUser(newUser);
        return GeneralStorage.lastAddedUsername;
    }

    public void updateEmployee(Employee employee) {
        try {
            addDepartments(Collections.singletonList(employee.getDepartment()));
            int newDepartmentId = getDepartmentId(employee.getDepartment());

            WorkHours tmp = new WorkHours();
            tmp.setType(employee.getWorkHoursType());
            tmp.setHours(employee.getWorkHoursPer100Days());
            addWorkHours(Collections.singletonList(tmp));
            int newWorkHoursId = getWorkHoursId(tmp);

            String sql = "UPDATE employee SET name = ?, department_id = ?, work_hours_id = ?\n" +
                    "WHERE id = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, employee.getName());
            statement.setInt(2, newDepartmentId);
            statement.setInt(3, newWorkHoursId);
            statement.setInt(4, employee.getId());
            statement.executeUpdate();

            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*
     * this method actually doesn't delete any data,
     * it just disables the employee's login
     */
    public void deleteEmployee(int id) {
        try {
            // sets active to 0 in Employees table
            String sql = "UPDATE employee SET active = 0 WHERE id = ?";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();

            // sets enabled to 0 in credentials
            sql = "SELECT credentials_id FROM employee WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();
            results.next(); // fetch the row
            int credentialsId = results.getInt(1);

            sql = "UPDATE credentials SET enabled = 0 WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, credentialsId);
            statement.executeUpdate();

            statement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addProject(Project project) {
        try {
            String sql =
                    "INSERT INTO projects(abbr, name, full_name, company) SELECT ?, ?, ?, ?\n" +
                            "    WHERE NOT EXISTS (SELECT * FROM projects WHERE abbr = ? AND name = ? AND full_name = ? AND company = ?)";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, project.getAbbr());
            statement.setString(2, project.getName());
            statement.setString(3, project.getFullName());
            statement.setString(4, project.getCompanyName());
            statement.setString(5, project.getAbbr());
            statement.setString(6, project.getName());
            statement.setString(7, project.getFullName());
            statement.setString(8, project.getCompanyName());

            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<ExcelRow> getAllLogsInPeriod(Integer periodId) {
        List<ExcelRow> rows = new ArrayList<>();
        try {
            String sql =
                    "SELECT employee.name, projects.abbr, projects.name, projects.company, log.service, log.details, log.hours_worked_1000times FROM timesheet.log\n" +
                            "\tINNER JOIN employee ON log.employee_id = employee.id\n" +
                            "\tINNER JOIN projects ON log.project_id = projects.id\n" +
                            "\tWHERE period_id = ?;";
            Connection connection = initConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, periodId);
            ResultSet results = statement.executeQuery();

            while(results.next()) {
                ExcelRow row = new ExcelRow();
                row.setEmployeeName(results.getString(1));
                row.setProject(results.getString(2), results.getString(3));
                row.setCompany(results.getString(4));
                row.setService(results.getString(5));
                row.setDetails(results.getString(6));
                row.setHoursWorked(results.getInt(7) / 1000.0);
                rows.add(row);
            }

            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rows;
    }
}
