const express = require('express');
const app = express();

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({extended: true}));

const user= require('./restful/user.js');
const gps= require('./restful/gps.js');
const search= require('./restful/search.js');

app.use('/',user);
app.use('/',gps);
app.use('/',search);

app.listen(13456, () => {
    console.log('server running 13456 port');
});