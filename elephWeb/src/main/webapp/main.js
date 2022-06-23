import { TimeSheetCell } from './tsc.js'
import { mrSock } from './mrsock.js'

const tbody = document.getElementById("mrtimesheet").getElementsByTagName('tbody')[0];

let queryString = window.location.search;
let params = new URLSearchParams(queryString);
window.login = params.get("login");


const cellsock = new mrSock("ws://" + location.host + "/elephWeb/Tsc/" + login, {type: "cell-list", payload: { dummy: "is pharoah"} });

// onmessage callback
function gotcells(payload) {
	const row = tbody.querySelector("tr[id='" + payload.projid + "']");
//	const row = tbody.getElementsByTagName('tr')[0];
	
  //console.log(payload);
    let newCell = row.insertCell();

	let cellElem = document.createElement('ts-cell');
	cellElem.setAttribute('timesheet-id', payload.cellid);
	newCell.appendChild(cellElem);
}

function gotrow(payload) {
	let newrow = tbody.insertRow();
	newrow.setAttribute('id', payload.projid);
	let newcell = newrow.insertCell();
	newcell.innerText = payload.pname;
}

function showusername(payload) {
	
  //console.log(payload);

	const un = document.getElementById("username");
	un.innerText = payload.user;
}
cellsock.registerCallback("username", showusername);
cellsock.registerCallback("cell-list", gotcells);
cellsock.registerCallback("row-list", gotrow);