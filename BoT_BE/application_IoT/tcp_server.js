const net = require('net');
const { FileSystemWallet, Gateway } = require('fabric-network');
const fs = require('fs');
const path = require('path');

const ccpPath = path.resolve(__dirname, '..', '..', 'basic-network', 'connection.json');
const ccpJSON = fs.readFileSync(ccpPath, 'utf8');
const ccp = JSON.parse(ccpJSON);

const ipaddr = "192.168.35.46";
const port = 3002;


let server = net.createServer(function (socket) {
	console.log(socket.address().address + " connected.");

	// setting encoding
	socket.setEncoding('utf8');

	// print data from client
	socket.on('data', function (data) {
		try {
			
			await callChainCode('gasStateUpdate', true, ...args);
		} catch(err) {
			console.log(err);
		}

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
	console.log('listening on 3002..');
});



// Call Chaincode
async function callChainCode(fnName, isSubmit, ...args) {
	try {
	  // Create a new file system based wallet for managing identities.
	  const walletPath = path.join(process.cwd(), 'wallet');
	  const wallet = new FileSystemWallet(walletPath);
	  console.log(`Wallet path: ${walletPath}`);
  
	  // Check to see if we've already enrolled the user.
	  const userExists = await wallet.exists('user2');
	  if (!userExists) {
		  console.log('An identity for the user "user2" does not exist in the wallet');
		  console.log('Run the registerUser.js application before retrying');
		  return;
	  }
  
	  // Create a new gateway for connecting to our peer node.
	  const gateway = new Gateway();
	  await gateway.connect(ccp, { wallet, identity: 'user2', discovery: { enabled: false } });
  
	  // Get the network (channel) our contract is deployed to.
	  const network = await gateway.getNetwork('mychannel');
  
	  // Get the contract from the network.
	  const contract = network.getContract('botcc');
  
	  let result;
	  if(isSubmit) {
		result = await contract.submitTransaction(fnName, ...args);
		console.log('Transaction has been submitted.');
	  } else {
		result = await contract.evaluateTransaction(fnName, ...args);
		console.log(`Transaction has been evaluated. result: ${result.toString()}`);
	  }
	  return result;
  
	} catch(err) {
	  console.error(`Failed to create transaction: ${error}`);
	  return 'error occurred!!!';
	}
  }