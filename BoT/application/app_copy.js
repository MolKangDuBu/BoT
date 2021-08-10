// 모듈추가
const express = require('express');
const app = express();
var bodyParser = require('body-parser');

// 하이퍼레저 모듈추가+연결속성파일로드
const { FileSystemWallet, Gateway } = require('fabric-network');
const fs = require('fs');
const path = require('path');
const ccpPath = path.resolve(__dirname, '..', 'basic-network' ,'connection.json');
const ccpJSON = fs.readFileSync(ccpPath, 'utf8');
const ccp = JSON.parse(ccpJSON);

// 서버속성
const PORT = 3000;
const HOST = '0.0.0.0';

// app.use
app.use(express.static(path.join(__dirname, 'views')));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));


// 라우팅
// 1. 페이지라우팅
app.get('/', (req, res)=>{
    res.sendFile(__dirname + '/views/index.html');
})
app.get('/create', (req, res)=>{
    res.sendFile(__dirname + '/views/create.html');
})
app.get('/query', (req, res)=>{
    res.sendFile(__dirname + '/views/query.html');
})

// 2. REST
/*
app.post('/project', async(req, res)=>{
    console.log("client request sended");
    console.log('key = ' + req.body.key);
    res.send(req.body.key);
})
*/


//set
app.post('/asset', async(req, res)=>{
    const name = req.body.name;
    const value = req.body.value;

    const walletPath = path.join(process.cwd(), 'wallet');
    const wallet = new FileSystemWallet(walletPath);
    console.log(`Wallet path: ${walletPath}`);

    const userExists = await wallet.exists('user1');
    if (!userExists) {
        console.log('An identity for the user "user1" does not exist in the wallet');
        console.log('Run the registerUser.js application before retrying');
        return;
    }
    const gateway = new Gateway();
    await gateway.connect(ccp, { wallet, identity: 'user1', discovery: { enabled: false } });
    const network = await gateway.getNetwork('mychannel');
    const contract = network.getContract('botcc');

    const result = await contract.submitTransaction('setAsset', name, value);
    await gateway.disconnect();

    
    console.log(result.toString());
    //res.status(200).send('Transaction has been submitted');
    //res.status(200).json(result);

    if (result.toString() === "incorrectArg") {
        console.log(`Something is wrong: Incorrect Arguments`);
        res.status(200).json(result.toString());
    } else if (result.toString() === "alreadyExist") {
        console.log(`Something is wrong: Already Exsisting ID`);
        res.status(200).json(result.toString());
    } else if(result.toString() === "putFailed") {
        console.log(`Something is wrong: putState Failed`);
        res.status(200).json(result.toString());
    } else {
        console.log('Transaction has been submitted');
        res.status(200).json(result.toString());
    }
})

//get
app.get('/asset', async(req, res)=>{
    const name = req.query.name;
    
    const walletPath = path.join(process.cwd(), 'wallet');
    const wallet = new FileSystemWallet(walletPath);
    console.log(`Wallet path: ${walletPath}`);
    const userExists = await wallet.exists('user1');
    if (!userExists) {
        console.log('An identity for the user "user1" does not exist in the wallet');
        console.log('Run the registerUser.js application before retrying');
        return;
    }
    const gateway = new Gateway();
    await gateway.connect(ccp, { wallet, identity: 'user1', discovery: { enabled: false } });
    const network = await gateway.getNetwork('mychannel');
    const contract = network.getContract('botcc');

    const result = await contract.evaluateTransaction('getAsset', name);
    console.log(`Transaction has been evaluated, result is: ${result.toString()}`);

    //var obj = JSON.parse(result);
    //res.status(200).json(obj);
    res.status(200).json(result);
})


// 서버시작
app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);