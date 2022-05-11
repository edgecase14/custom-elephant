import { mrSock } from './mrsock.js'

export class TimeSheetCell extends HTMLElement {

  static handlers=[];
  
  static() {
	// new mrSock here?
  }

   // Can define constructor arguments if you wish.
  constructor() {
    // If you define a constructor, always call super() first!
    // This is specific to CE and required by the spec.
    super();
    
    //console.log(":yey:");
	this.sock = new mrSock("ws://" + location.host + "/elephWeb/Tsc/jjackson"); // need singleton - try class static fn()?

	// Create a shadow root
	this.attachShadow({mode: 'open'}); // sets and returns 'this.shadowRoot'

	// Create (nested) span elements
	const wrapper = document.createElement('div');
	wrapper.setAttribute('contenteditable','true');
	wrapper.innerText = "0";

	// Create some CSS to apply to the shadow dom
//	const style = document.createElement('style');
//	style.textContent = '';

    // attach the created elements to the shadow DOM
	this.shadowRoot.append(wrapper);

	// Apply external styles to the shadow dom
	const linkElem = document.createElement('link');
	linkElem.setAttribute('rel', 'stylesheet');
	linkElem.setAttribute('href', 'tsc.css');

	// Attach the created element to the shadow dom
	this.shadowRoot.appendChild(linkElem);

 
    this.addEventListener("keydown", (e) => {
       if (e.keyCode == '13') {
          //console.log("oh no you di-int");
	  	  this.shadowRoot.querySelector("div").className = "";
	      this.shadowRoot.querySelector("div").className = "bloody";
	      e.preventDefault();
	      this.blur();
	  
	      let cell_id = Number(this.getAttribute("timesheet-id"));
	      let contents = this.shadowRoot.querySelector("div").innerText;
	      // console.log(contents);
	      this.sock.send({ type:"cell-update", payload: {id: cell_id, contents: contents }});

	  	  return false;
       }
    });
  
  } // constructor !!

    ack(payload) { // payload not used - maybe "cell-init" will use?
      console.log("cell-update:: " + JSON.stringify(payload));
      if (payload.action == "ack") { 
       this.shadowRoot.querySelector("div").className = "rolling-meadows";
        const myid = this.getAttribute("timesheet-id");
      }
      if (payload.action == "init") {
		this.shadowRoot.querySelector("div").innerText = payload.contents;
	  }
    }
    
    mymessage(payload) { // can javascript have static methods?
		for (let handler of TimeSheetCell.handlers) {
					//console.log("trying handler id: " + handler.ep + " json.id" + jsondata.payload.id);
					if (handler.ep == payload.id) {
						handler.cb(payload);
						break; // maybe we want more than one callback?
					}
		}
	}
	
    connectedCallback() {
      if (this.hasAttribute("timesheet-id")) {
        const myid = Number(this.getAttribute("timesheet-id"));
        // console.log("my id is : " + myid);
		this.shadowRoot.querySelector("div").setAttribute('id', myid); // maybe some kind of introspection instead?
	    TimeSheetCell.handlers.push({ep: myid, cb: this.ack.bind(this)});
		this.sock.registerCallback("cell-update", this.mymessage.bind(this)); // duplicates every one XXX
		// get saved value from backend database
		this.sock.send({ type:"cell-init", payload: {id: myid}});
      } else {
		console.log("error: ts-cell: attribute timesheet-id is required when element is attached to DOM");
	  }
    }
 
 }

customElements.define('ts-cell', TimeSheetCell);