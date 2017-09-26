const express=require('express');
const router = express.Router();

var connection= require('../mysql.js');

router.route('/user/login').post((req,res)=>{
    const paramId= req.body.id;
    const paramPwd= req.body.password;

    console.log('/user/login : '+paramId);

    const sqlIdQuery='select id from user where id = ?';
    const sqlPwdQuery='select password from user where password = ?';
    
    connection.query(sqlIdQuery,[paramId],(err,idResult)=>{
        if(err){
            console.log(err);
            res.sendStatus(400);
        }
        else if(idResult==0){
            console.log('is not user : '+paramId);
            res.sendStatus(201);
        }else{
            connection.query(sqlPwdQuery,[paramPwd],(err,pwdResult)=>{
                if(err){
                    console.log(err);
                    res.sendStatus(400);
                }else{
                    console.log('user login : '+paramId);
                    res.sendStatus(200);
                
                }
            })
        }
    })
    
});

router.route('/user/register').post((req,res)=>{

  
    const paramId= req.body.id;
    const paramPwd= req.body.password;
    const paramPhone= req.body.phone;
    const paramName= req.body.name;

    console.log('/user/register : '+paramId);
    const input={
        'id':paramId,
        'password':paramPwd,
        'name':paramName,
        'phone':paramPhone
    }
    const sqlInputQuery='insert into user set ?';
    const sqlIdQuery='select id from user where id=?';
    
    connection.query(sqlIdQuery,[paramId],(err,idResult)=>{
        if(err){
            console.log(err);
            res.sendStatus(400);
        }
        else if(idResult==''){
            connection.query(sqlInputQuery,input,(err,inputResult)=>{
                if(err){
                    console.log(err);
                    res.sendStatus(400);
                }else{
                    console.log('user insert : '+paramId);
                    res.sendStatus(200);
                
                }
            })
        }else{
            
            console.log('is not ok id : '+paramId);
            console.log(idResult);
            res.sendStatus(201);
            
        }
    })

})

module.exports = router;
