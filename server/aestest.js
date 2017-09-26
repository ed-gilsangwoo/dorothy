var AES = require('aes')

// var key = [0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xfffffff8];
var temp='01099051726';
var key=temp.toString(16);
var pt = [0x00000000,0x00000000,0x00000000,0x00000000];
var ct = [0xd241aab0,0x5a42d319,0xde81d874,0xf5c7b90d];

var aes = new AES(key);
console.dir(aes.encrypt(pt)); // => [0xd241aab0,0x5a42d319,0xde81d874,0xf5c7b90d] 
console.dir(aes.decrypt(ct)); 
