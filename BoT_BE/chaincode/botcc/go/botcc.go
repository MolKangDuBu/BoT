// package name
package main

 // module import
import (
	"bytes"
	"fmt"
	"strconv"
	"encoding/json"
 	
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
 )
 
 // class, struct define
type SmartContract struct {
 }

type User struct {
	Key		string `json:"key"`
	Id		string `json:"id"`
	Pw		string `json:"pw"`
	Name	string `json:"name"`
	Mail	string `json:"mail"`
	Phone	string `json:"phone"`
	Team	string `json:"team"`
} 

type UserKey struct {
	Key string
	Idx int
}

type IoT struct {
	Devicekey	string `json:"devicekey"`
	Device	string `json:"device"`
	UserId		string `json:"userid"`
	Area	string `json:"area"`
	Lamp	bool `json:"lamp"`
	Gas		float64 `json:"gas"`
	Tmp		int `json:"tmp"`
	Hum		int `json:"hum"`
	Date	string `json:"date"`
	Feedback	string `json:"feedback"`
 } 

type IotKey struct {
	Devicekey string
	Idx int
}

 // Init function
func (t *SmartContract) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

 // Invoke function
func (t *SmartContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	fn, args := stub.GetFunctionAndParameters()

	if fn == "addUser" {
		return t.addUser(stub, args)
	} else if fn == "login" {
		return t.login(stub, args)
	} else if fn == "queryAllUsers" {
		return t.queryAllUsers(stub)
	} else if fn == "getUser" {
		return t.getUser(stub, args)
	} else if fn == "addIot" {
		return t.addIot(stub, args)
	} else if fn == "lampStateUpdate" {
		return t.lampStateUpdate(stub, args)
	} else if fn == "gasStateUpdate" {
		return t.gasStateUpdate(stub, args)
	} else if fn == "tmpHumStateUpdate" {
		return t.tmpHumStateUpdate(stub, args)
	} else if fn == "queryAllIoTs" {
		return t.queryAllIoTs(stub)
	}

	return shim.Error("Invalid Smart Contract function name.")
}


/* ------------------------- USER functions ------------------------- */

// Add User function
 func (t *SmartContract) addUser(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 6 {
		return shim.Error("Incorrect number of arguments. Expecting 6")
	}

	// checking duplication userId
	_, isExist := userFind(stub, args, "addUser")
	if isExist == true {
		return shim.Error("Already Existing Id.")
	}

	var userkey = UserKey{}
	json.Unmarshal(userKeyGenerate(stub), &userkey)
	keyIdx := strconv.Itoa(userkey.Idx)

	var keyString = userkey.Key + keyIdx
	var user = User{Key: keyString, Id: args[0], Pw: args[1], Name: args[2], Mail: args[3], Phone: args[4], Team: args[5]}
	userAsBytes, _ := json.Marshal(user)
	
	err := stub.PutState(keyString, userAsBytes)
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to record user catch: %s", userkey))
	}
	userKeyAsBytes, _ := json.Marshal(userkey)
	stub.PutState("latestUserKey", userKeyAsBytes)

	return shim.Success(nil)
}

// Login function
func (t *SmartContract) login(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if(len(args) != 2) {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	user, isExist := userFind(stub, args, "login")
	if isExist == false {
		return shim.Error("Incorrect ID: Not Exist")
	}
	if user.Pw != args[1] {
		return shim.Error("Incorrect PW")
	}

	userAsBytes, _ := json.Marshal(user)
	return shim.Success(userAsBytes)
}

// Query All Users function
func (t *SmartContract) queryAllUsers(stub shim.ChaincodeStubInterface) peer.Response {
	userKeyAsBytes, _ := stub.GetState("latestUserKey")
	userkey := UserKey{}
	json.Unmarshal(userKeyAsBytes, &userkey)
	idxStr := strconv.Itoa(userkey.Idx + 1)

	var startKey = "USER0"
	var endKey = userkey.Key + idxStr

	resultIter, err := stub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultIter.Close()

	var buffer bytes.Buffer
	buffer.WriteString("[")
	bArrayMemberAlreadyWritten := false

	for resultIter.HasNext() {
		queryResponse, err := resultIter.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")
	
		// Record is a JSON object, so we write as-is
		buffer.WriteString(", \"Record\":")		
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}

	buffer.WriteString("]")
	return shim.Success(buffer.Bytes())
}

// Get One User's function
func (t *SmartContract) getUser(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	userAsBytes, _ := stub.GetState(args[0])
	return shim.Success(userAsBytes)
}

// Find User's Id function
func userFind(stub shim.ChaincodeStubInterface, args []string, fns string) (User, bool) {
	userKeyAsBytes, _ := stub.GetState("latestUserKey")
	userkey := UserKey{}
	json.Unmarshal(userKeyAsBytes, &userkey)
	idxStr := strconv.Itoa(userkey.Idx + 1)

	var startKey = "USER0"
	var endKey = userkey.Key + idxStr

	resultIter, err := stub.GetStateByRange(startKey, endKey)
	if err != nil {
		fmt.Println(err.Error())
	}
	defer resultIter.Close()


	for resultIter.HasNext() {
		queryResponse, err := resultIter.Next()
		if err != nil {
			fmt.Println(err.Error())
		}
		
		user := User{}
		json.Unmarshal(queryResponse.Value, &user)

		if user.Id == args[0] {
			if fns == "login" {
				return user, true;
			} else if fns == "addUser" {
				return User{}, true
			}				
		}		
	}

	return User{}, false
}

// Generate User's Key function
func userKeyGenerate(stub shim.ChaincodeStubInterface) []byte {
	var isFirst bool = false

	userKeyAsBytes, err := stub.GetState("latestUserKey")
	if err != nil {
		fmt.Println(err.Error())
	}

	userkey := UserKey{}
	json.Unmarshal(userKeyAsBytes, &userkey)
	var tempIdx string
	tempIdx = strconv.Itoa(userkey.Idx)
	if len(userkey.Key) == 0 || userkey.Key == "" {
		isFirst = true
		userkey.Key = "USER"
	}
	if !isFirst {
		userkey.Idx = userkey.Idx + 1
	}
	fmt.Println("Last UserKey is " + userkey.Key + " : " + tempIdx)

	returnUserBytes, _ := json.Marshal(userkey)
	return returnUserBytes	
}


/* ------------------------- IoT functions ------------------------- */
// Add IoT Info
func (t *SmartContract) addIot(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 9 {
		return shim.Error("Incorrect number of arguments. Expecting 9")
	}
	
	var iotkey = IotKey{}
	json.Unmarshal(iotKeyGenerate(stub), &iotkey)
	keyIdx := strconv.Itoa(iotkey.Idx)

	var lamp bool = flase
	var gas float64 = 0
	var tmp int = 0
	var hum int = 0
	var err error

	if args[3] != "" {
		lamp, err = strconv.ParseBool(args[3])
	} 
	
	if args[4] != "" {
		gas, err = strconv.ParseFloat(args[4], 64)
	} 
	
	if args[5] != ""  {
		tmp, err = strconv.Atoi(args[5])
		hum, err = strconv.Atoi(args[6])
	}

	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to record IoT catch: %s", iotkey))
	}

	var keyString = iotkey.Devicekey + keyIdx
	var iot = IoT{Devicekey: keyString, Device: args[0], UserId: args[1], Area: args[2], Lamp: lamp, Gas: gas, Tmp: tmp, Hum: hum, Date: args[7], Feedback: args[8]}
	iotAsBytes, _ := json.Marshal(iot)
	
	
	err = stub.PutState(keyString, iotAsBytes)
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to record IoT catch: %s", iotkey))
	}
	iotKeyAsBytes, _ := json.Marshal(iotkey)
	stub.PutState("latestIotKey", iotKeyAsBytes)

	return shim.Success(nil)
}


// Lamp State Update (on, off)
func (t *SmartContract) lampStateUpdate(stub shim.ChaincodeStubInterface, args[] string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	iotAsBytes, _ := stub.GetState(args[0])
	if iotAsBytes == nil {
		return shim.Error("Could not locate IoT")
	}
	
	iot := IoT{}
	json.Unmarshal(iotAsBytes, &iot)
	iot.Lamp, _ = strconv.ParseBool(args[1])
	iotAsBytes, _ = json.Marshal(iot)
	err := stub.PutState(args[0], iotAsBytes)
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to change IoT holder: %s", args[0]))
	}
	return shim.Success(nil)

}

// Gas State Update (0.0, 1.4...)
func (t *SmartContract) gasStateUpdate(stub shim.ChaincodeStubInterface, args[] string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	iotAsBytes, _ := stub.GetState(args[0])
	if iotAsBytes == nil {
		return shim.Error("Could not locate IoT")
	}
	
	iot := IoT{}
	json.Unmarshal(iotAsBytes, &iot)
	iot.Gas, _ = strconv.ParseFloat(args[1], 64)
	iotAsBytes, _ = json.Marshal(iot)
	err := stub.PutState(args[0], iotAsBytes)
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to change IoT holder: %s", args[0]))
	}
	return shim.Success(nil)

}

// Temperature and Humidity State Update
func (t *SmartContract) tmpHumStateUpdate(stub shim.ChaincodeStubInterface, args[] string) peer.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}
	iotAsBytes, _ := stub.GetState(args[0])
	if iotAsBytes == nil {
		return shim.Error("Could not locate IoT")
	}
	
	iot := IoT{}
	json.Unmarshal(iotAsBytes, &iot)
	iot.Tmp, _ = strconv.Atoi(args[1])
	iot.Hum, _ = strconv.Atoi(args[2])
	iotAsBytes, _ = json.Marshal(iot)
	err := stub.PutState(args[0], iotAsBytes)
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to change IoT holder: %s", args[0]))
	}
	return shim.Success(nil)

}

// Show All State of IoT
func (t *SmartContract) queryAllIoTs(stub shim.ChaincodeStubInterface) peer.Response {
	iotKeyAsBytes, _ := stub.GetState("latestIotKey")
	iotkey := IotKey{}
	json.Unmarshal(iotKeyAsBytes, &iotkey)
	idxStr := strconv.Itoa(iotkey.Idx + 1)

	var startKey = "IoT0"
	var endKey = iotkey.Devicekey + idxStr

	resultIter, err := stub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultIter.Close()

	var buffer bytes.Buffer
	buffer.WriteString("[")
	bArrayMemberAlreadyWritten := false

	for resultIter.HasNext() {
		queryResponse, err := resultIter.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")
	
		// Record is a JSON object, so we write as-is
		buffer.WriteString(", \"Record\":")		
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}

	buffer.WriteString("]")
	return shim.Success(buffer.Bytes())
}

//IotKey generateKey function
func iotKeyGenerate(stub shim.ChaincodeStubInterface) []byte {
	var isFirst bool = false

	iotKeyAsBytes, err := stub.GetState("latestIotKey")
	if err != nil {
		fmt.Println(err.Error())
	}

	iotkey := IotKey{}
	json.Unmarshal(iotKeyAsBytes, &iotkey)
	var tempIdx string
	tempIdx = strconv.Itoa(iotkey.Idx)
	if len(iotkey.Devicekey) == 0 || iotkey.Devicekey == "" {
		isFirst = true
		iotkey.Devicekey = "IoT"
	}
	if !isFirst {
		iotkey.Idx = iotkey.Idx + 1
	}
	fmt.Println("Last IoTKey is " + iotkey.Devicekey + " : " + tempIdx)

	returnIotBytes, _ := json.Marshal(iotkey)
	return returnIotBytes	
}

 // main function
func main() {	
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Println("Error creating new Smart Cont9ract : %s", err)
	}
}

