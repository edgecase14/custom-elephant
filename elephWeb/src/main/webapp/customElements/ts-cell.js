export class TimeSheetCell extends HTMLElement {
  static handlers=[];
  static my_elem_name = 'ts-cell';
  static init() {  // static block seemed to run before mrsock got imported in main.js
	  window.mrsock.registerCallback("cell-update", this.mymessage)
//	  console.log('register cell-update cb')
  }

   // Can define constructor arguments if you wish.
  constructor() {
    // If you define a constructor, always call super() first!
    // This is specific to CE and required by the spec.
    super();
    
	// Create a shadow root
	this.attachShadow({mode: 'open'}); // sets and returns 'this.shadowRoot'

	// Create some CSS to apply to the shadow dom
//	const style = document.createElement('style');
//	style.innerHTML = `


	const sheet = new CSSStyleSheet
	sheet.replaceSync( `
* {
	margin: .5em;
	display: inline-block;
	border: 1px solid #ddd;
	font-family: arial;
	background-color: black;
	color: goldenrod;
}

div {
	padding: 10px;
	max-width: 8em;
	text-overflow: elipsis;
}

.foobar {
	background-color: white;
	color: black;
}

.bloody {
	background-color: red;
	color: white;
}

.rolling-meadows {
	background-color: #88ef99;
	color: black;
}
.is-stat {
	background-color: #68cf79;
	color: black;
`)
	this.shadowRoot.adoptedStyleSheets = [ sheet ]

//}`;
	//this.shadowRoot.appendChild(style);

	// Create (nested) span elements
	const wrapper = document.createElement('div');
	//wrapper.setAttribute('id', 'entry_el');
	wrapper.setAttribute('contenteditable','true');
	wrapper.innerText = "0";
	this.shadowRoot.appendChild(wrapper);

	const note_el = document.createElement('div');
	note_el.setAttribute('id', 'note_el');
	note_el.setAttribute('contenteditable','true');
	note_el.innerText = "notes go here!";
	this.shadowRoot.append(note_el);

	// make this conditional on customElement Attribute, "hide_date"?
	//const date_el = document.createElement('div');
	//date_el.setAttribute('id', 'date_el');
	//date_el.innerText = "YYYY-MM-DD";
	//this.shadowRoot.append(date_el);

// this was inlined above for performance reasons
	// Apply external styles to the shadow dom
//	const linkElem = document.createElement('link');
//	linkElem.setAttribute('rel', 'stylesheet');
//	linkElem.setAttribute('href', 'tsc.css');
//	this.shadowRoot.appendChild(linkElem);

 
    this.addEventListener("keydown", (e) => {
       if (e.keyCode == '13') {
          //console.log("oh no you di-int");
	  	  this.shadowRoot.querySelector("div").className = "";
	      this.shadowRoot.querySelector("div").className = "bloody";
	      e.preventDefault();
	      this.blur();
	  
	      let cell_id = Number(this.getAttribute("timesheet-id"));
	      let contents = this.shadowRoot.querySelector("div").innerText;
	      let note_str = this.shadowRoot.querySelector("div[id='note_el']").innerText;
	      // console.log(contents);
	      // need to add "compoment": "ts-cell" here and in Tsc.java, and maybe not use raw json here
	      window.mrsock.send({ type:"cell-update", payload: {id: cell_id, contents: contents, note: note_str }});

	  	  return false;
       }
    });
  
  } // constructor !!

    ack(payload) { // payload not used - maybe "cell-init" will use?
      //console.log("cell-update:: " + JSON.stringify(payload));
      if (payload.action == "ack") { 
       this.shadowRoot.querySelector("div").className = "rolling-meadows";
        //const myid = this.getAttribute("timesheet-id");
      }
      if (payload.action == "init") {
		this.shadowRoot.querySelector("div").innerText = payload.contents;
		this.shadowRoot.querySelector("div[id='note_el']").innerText = payload.note;
		// make this conditional on customElement Attribute, "hide_date"?
		//this.shadowRoot.querySelector("div[id='date_el']").innerText = payload.date;
	  }
    }
    
    // move to superclass
    static mymessage(payload) {
		for (let handler of TimeSheetCell.handlers) {
					//console.log("trying handler id: " + handler.ep + " json.id" + jsondata.payload.id);
					if (handler.ep == payload.id) {
						handler.cb(payload);
						break; // maybe we want more than one callback?
					}
					// if none match, console.log()
		}
	}
	
	// move most to superclass
    connectedCallback() {
      if (this.hasAttribute("timesheet-id")) { // parameterize: $ce_type_name-id
        const myid = Number(this.getAttribute("timesheet-id"));
        // console.log("my id is : " + myid);
        // DOM id format $ce_type_name-id-$myid
		this.shadowRoot.querySelector("div").setAttribute('id', myid); // maybe some kind of introspection instead?
		// like search backwards the first enclosing custom-element (or root of shadow-dom?) and this.getAttribute
	    TimeSheetCell.handlers.push({ep: myid, cb: this.ack.bind(this)});
	    // move to ts-cell.js?
      } else {
		console.log("error: ts-cell: attribute timesheet-id is required when element is attached to DOM");
	  }
    }
 
 }

customElements.define('ts-cell', TimeSheetCell);
