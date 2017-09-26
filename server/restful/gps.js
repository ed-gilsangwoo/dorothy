const express=require('express');
const router = express.Router();
const connection= require('../mysql.js');



router.route('/gps/add').post((req,res)=>{
    const paramLat= req.body.lat;
    const paramLng= req.body.lng;
    const paramReporter= req.body.id;
    const paramWharkind= req.body.whatkind;
    const paramHowmuch= req.body.howmuch;
    
    // const ServiceKey='S5LllwrgI%2BSSm4dEH424vIeBfZL7ofEadMOTxYa3EXbzxVdpcEclYfsQ8jTCoDWS%2BZElzAgqF1I4N5a5r2aiXQ%3D%3D';
    // const usrName;
    // const usrHpNo;
    // const appName;
    // const dscNtTypeCode;
    // const dscfNtTitleCode;
    // const dscfnttitle;
    // const dscfNtContent;
    // const longitude=paramLng;
    // const latitude=paramLat;
    // const dscNtOpenYn='Y';
    // const url='http://openapi.gmap.go.kr/service/rest/roadDamgeRegService/setUnlawInfomation?ServiceKey='+ServiceKey+'&usrName='+usrName+'&usrHpNo='+usrHpNo+'&appName='+appName+'&dscNtTypeCode='+dscNtTypeCode+'&dscfNtTitleCode='+dscfNtTitleCode+'&dscfnttitle='+dscfnttitle+'&dscfNtContent='+dscfNtContent+'&longitude='+longitude+'&latitude='+latitude+'&dscNtOpenYn='+dscfNtOpenYn;

    console.log('/gps/add : '+paramReporter);
    
    var input={
        'lat':paramLat,
        'lng':paramLng,
        'reporter':paramReporter,
        'whatkind':paramWharkind,
        'howmuch':paramHowmuch,
    }
    // const sqlQuery='insert into maps set (lat,lng,reporter,whatkind,howmuch) value ?';
    
    const sqlQuery='insert into maps set ?';
    const sqlUpdateQuery='update user set rank=rank+1 where id = ?';
    connection.query(sqlQuery,input,(err,result)=>{
        if(err){
            console.log(err);
            res.sendStatus(400);
        }else{
            console.log('insert : '+paramLat+','+paramLng);
            connection.query(sqlUpdateQuery,paramReporter,(error,rankResult)=>{
                if(error){
                    console.log(error);
                    res.sendStatus(400);
                }else{
                    console.log('insert ok');
                    // console.log(rankResult);
                    res.sendStatus(200);
                    // request({
                    //     url
                    // },(apiError,response,body)=>{
                    //     if(apiError){
                    //         console.log(apiError);
                    //         res.sendStatus(400);
                    //     }else{ 
                    //         // var output={};
                    //         var output=[];
           
                    //      }
                    // })
                }
            })
        }
    })
});
router.route('/gps/show').post((req,res)=>{
    
    console.log('/gps/show');    
    // var input={
    //     'lat':paramLat,
    //     'ing':paramIng
    // };

    // const sqlQuery='SELECT *, ( 6371 * acos( cos( radians(?) ) * cos( radians( lat ) ) * cos( radians( ing ) - radians(?) ) + sin( radians(?) ) * sin( radians( lat ) ) ) ) AS distance FROM maps HAVING distance < 20 ORDER BY distance LIMIT 0 , 5;'
    // const sqlQuery='SELECT uid, ( 6371 * acos( cos( radians(?) ) * cos( radians( lat ) ) * cos( radians( ing ) - radians(?) ) + sin( radians(?) ) * sin( radians( lat ) ) ) ) AS distance FROM maps HAVING distance < 5 ORDER BY distance LIMIT 0 , 5;'
    
    const sqlQuery='select * from maps';
    // connection.query(sqlQuery,[paramLat,paramIng,paramLat],(err,result)=>{
    connection.query(sqlQuery,[],(err,result)=>{
            
        if(err){
            console.log(err);
            res.sendStatus(400);
        }else{
            console.log('show gps');
            res.send({
                'showKey':result
            });
        }
    })
})

module.exports = router;
