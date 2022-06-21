let keys = ['id', 'artistName', 'dateTime', 'place', 'availableSpots', 'soldSpots']
let url = "http://localhost:8080/myapp/festivals"

function getFormJson(){
    let myObj = {};
    for (let i = 0; i < keys.length; i++) {
        let key = keys[i];
        myObj[key] = document.getElementById(key).value
    }
    return JSON.stringify(myObj);
}

function create() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            handleResponse(this);
        }
    };
    xhttp.open("POST", url, true);
    xhttp.setRequestHeader('Content-Type', 'application/json');
    xhttp.send(getFormJson());
}

function readById() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            handleResponse(this);
        }
    };
    let id = document.getElementById("id").value;
    if (id === "") {
        badRequest();
        return;
    }
    xhttp.open("GET", url + "/" + id, true);
    xhttp.send();
}



function update() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            handleResponse(this);
        }
    };
    let id = document.getElementById("id").value;
    if (id === "") {
        badRequest();
        return;
    }
    xhttp.open("PUT", url + "/" + id, true);
    xhttp.setRequestHeader('Content-Type', 'application/json');
    xhttp.send(getFormJson());
}

function deleteById() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            handleResponse(this);
        }
    };
    let id = document.getElementById("id").value;
    if (id === "") {
        badRequest();
        return;
    }
    xhttp.open("DELETE", url + "/" + id, true);
    xhttp.send();
}

function readAll() {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4) {
            handleResponse(this);
        }
    };
    xhttp.open("GET", url, true);
    xhttp.send();
}

function badRequest() {
    let infoDiv = document.getElementById("infoDiv");
    infoDiv.innerHTML = "Error: No Id given;";
}

function handleResponse(response) {
    let text = ""
    if (400 <= response.status && response.status <= 404) {
        text += "Error:" + response.responseText;
    } else if (response.status === 200) {
        text += responseToTable(response.responseText);
    }
    let infoDiv = document.getElementById("infoDiv");
    text = text.replace(/\n/g, "<br/>");
    infoDiv.innerHTML = text;
}

function createHeaders() {
    let result = "<tr>";
    for (let i = 0; i < keys.length; i++) {
        result += "<th>" + keys[i] + "</th>";
    }
    result += "</tr>";
    return result;
}

function createItemRow(item) {
    let result = "<tr>";
    for (let i = 0; i < keys.length; i++) {
        result += "<td>" + item[keys[i]] + "</td>";
    }
    result += "</tr>";
    return result;
}

function autoComplete(json) {
    for (let i = 0; i < keys.length; i++) {
        document.getElementById(keys[i]).value = json[keys[i]];
    }
}

function responseToTable(responseText) {
    let json = JSON.parse(responseText);
    let table = "<table>";
    if (!Array.isArray(json)) {
        autoComplete(json);
        json = [json];
    }
    if (json.length > 0) {
        table += createHeaders();
        for (let i = 0; i < json.length; i++) {
            table += createItemRow(json[i]);
        }
    }
    table += "</table>";
    return table;
}