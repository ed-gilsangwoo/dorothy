const mysql = require('mysql');

const connection = mysql.createConnection({
    host: 'localhost',
    port: 3306,
    database: 'dorothy',
    user: 'root',
    password: '1234'
});

connection.connect();

module.exports = connection;
