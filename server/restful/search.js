const express=require('express');
const router = express.Router();
const connection= require('../mysql.js');

var request= require('request');

router.route('/search').post((req,res)=>{
    console.log('/search');
    const paramSearch= req.body.search;
    const key= 'AIzaSyA5ZNmvMZ9M_7AjsjB1OvOWZSUrSUtD7TY';

    const url='https://maps.googleapis.com/maps/api/place/textsearch/json?query='+encodeURIComponent(paramSearch)+'&key='+key;
    request({
        url
    },(error,response,body)=>{
        if(error){
            console.log(error);
            res.sendStatus(400);
        }else{ 
            // var output={};
            var output=[];
            var getOutput= JSON.parse(body);
            for(var i=0;i< getOutput.results.length;i++){
                var temp=getOutput.results[i];
                
                var tempLat=temp.geometry.location.lat;
                var tempLng=temp.geometry.location.lng;
                var tempName=temp.name;
                var tempAddress=temp.formatted_address;
                var tempOutput={
                    'lat':tempLat,
                    'Lng':tempLng,
                    'name':tempName,
                    'address':tempAddress
                };
                output.push(tempOutput);
            }
            console.log('serarch ok');
            // for()?
            res.send({
                'searchKey':output
            });
        }
    })
    
});

module.exports = router;
