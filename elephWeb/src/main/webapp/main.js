import { TimeSheetCell } from './tsc.js'
import { mrSock } from './mrsock.js'

const days_header = document.getElementById("mrtimesheet").getElementsByTagName("thead")[0].querySelector("tr[id='days']");

const days_footer = document.getElementById("mrtimesheet").getElementsByTagName("tfoot")[0].querySelector("tr[id='day_totals']");
for (let day = 1; day <= 16; day++) {
	let dh_cell = days_header.insertCell();
	dh_cell.setAttribute('id', "day" + day);
	if (day == 1) {
		dh_cell.innerText = "day" + day + " STAT HOLIDAY";
		dh_cell.className = "is-stat";
	} else {
		dh_cell.innerText = "day" + day;
	}
	let df_cell = days_footer.insertCell();
	df_cell.setAttribute('id', "day" + day);

}
	
const tbody = document.getElementById("mrtimesheet").getElementsByTagName('tbody')[0];

let queryString = window.location.search;
let params = new URLSearchParams(queryString);
window.login = params.get("login");


const cellsock = new mrSock("ws://" + location.host + "/elephWeb/Tsc/" + login, {type: "cell-list", payload: { dummy: "is pharoah"} });

// onmessage callbacks
function gotcells(payload) {
    console.log(payload);
    
	// could be more specific, see days_header above
	const row = tbody.querySelector("tr[id='" + payload.projid + "']");
	// is there another attribute besides 'id' we can use?  it pollutes a document global namespace
    const day = payload.date.slice(-2); // get day of month
	const targetCell = row.querySelector("td[id='" + Number(day) + "']");

	const filler = targetCell.querySelector("div[id='filler']");
	if (filler != null) {
		targetCell.removeChild(filler);
	}

	let cellElem = document.createElement('ts-cell');
	cellElem.setAttribute('timesheet-id', payload.cellid);
	// set attribute for hide-date, has ot, is-stat-day, is mandatory-stat-ot

	const addot = targetCell.querySelector("div[id='addot']");
	targetCell.insertBefore(cellElem, addot);
	let br = document.createElement('br');
	targetCell.insertBefore(br, addot);
}

function gotrow(payload) {
	// can we build the row first, then insert into live DOM?
	let proj_row = tbody.insertRow();
	proj_row.setAttribute('id', payload.projid);
	let job_id = proj_row.insertCell();
	job_id.innerText = payload.job_id;
	let job_name = proj_row.insertCell();
	job_name.innerText = payload.pname;
	// TODOq use payload.days_in_period to make a row of empty cells
	for (let day = 1; day <= 16; day++) {
		let te_cell = proj_row.insertCell();
		te_cell.setAttribute('id', day);

		let filler = document.createElement('div');
		filler.setAttribute('id', "filler");
		filler.innerText = "empty cell";
		te_cell.appendChild(filler);

	    //te_cell.innerText = "<div id='addot'>Add Overtime</div>";
		let addot = document.createElement('div');
		addot.setAttribute('id', "addot");
		addot.innerText = "Add Overtime";
		te_cell.appendChild(addot);
	}
}

function showusername(payload) {
	
  //console.log(payload);

	const un = document.getElementById("username");
	un.innerText = payload.user;
}
cellsock.registerCallback("username", showusername);
cellsock.registerCallback("cell-list", gotcells);
cellsock.registerCallback("row-list", gotrow);