const net = require('net');
const ipaddr = "192.168.35.236";
const port = 3001;

let server = net.createServer(function (socket) {
	console.log(socket.address().address + " connected.");

	// setting encoding
	socket.setEncoding('utf8');

	// print data from client
	socket.on('data', function (data) {
		console.log(data);
	});

	// print message for disconnection with client
	socket.on('close', function () {
		console.log('client disconnted.');
	});

	// send message to client
	setTimeout(() => {
		socket.write('welcome to server');
	}, 500);
	
	setTimeout(() => {
		socket.destroy();
	}, 3000);
});

// print error message
server.on('error', function (err) {
	console.log('err: ', err.code);
});

// listening
server.listen(port, ipaddr, function () {
	console.log('listening on 2031..');
});