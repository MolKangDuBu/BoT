var net = require('net');

var socket = net.connect({
    port: 22485,
    host: "192.168.35.18"
});

socket.setEncoding('utf8');

socket.on('connect', function() {
    console.log("on connect");

    setTimeout(() => {
        socket.write('ON');
    }, 1000);

    setTimeout(() => {
        socket.write('OFF');
    }, 5000);

});
socket.on('data', function(data) {
    console.log(data);
});

socket.on('close', function() {
    console.log('close');
});


socket.on('error', function(err) {
    console.log('on error: ', err.code);
});