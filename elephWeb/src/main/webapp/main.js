import { TimeSheetCell } from './tsc.js'
import { mrSock } from './mrsock.js'

const row = document.getElementById("row1");

let queryString = window.location.search;
let params = new URLSearchParams(queryString);
window.login = params.get("login");


const cellsock = new mrSock("ws://" + location.host + "/elephWeb/Tsc/" + login, {type: "cell-list", payload: { dummy: "is pharoah"} });

// onmessage callback
function gotcells(payload) {
	
  //console.log(payload);

	let cellElem = document.createElement('ts-cell');
	cellElem.setAttribute('timesheet-id', payload.cellid);
	row.appendChild(cellElem);
}

function showusername(payload) {
	
  //console.log(payload);

	const un = document.getElementById("username");
	un.innerText = payload.user;
}

cellsock.registerCallback("cell-list", gotcells);
cellsock.registerCallback("username", showusername);