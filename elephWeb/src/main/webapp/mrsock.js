/**
 * giving socket.io a run for it's money?
 */
export class mrSock {
	
//  url;
	sock;
	dispatch;
	statusElement;
	onOpenMsg;
	
  	constructor (url,openmsg) {
//		this.url = url; // could we just get this from WebSocket?
		this.dispatch = [];
		this.onOpenMsg = openmsg;
		this.sock = new WebSocket(url);
		//console.log(url);

		const sockStatus = document.getElementById("tsc"); // is there a way to pass this, maybe class factory?
		// XXX skip if not present in DOM
		this.statusElement = document.createElement('p')
		//console.log(sockStatus.innerText);
		this.statusElement.innerText = url + " setup";
		sockStatus.appendChild(this.statusElement);
  

  		const ono = function(event) {
			this.statusElement.innerText = this.url + " open";
			this.send(this.onOpenMsg);
  		}
		this.sock.onopen = ono.bind(this);  // does this race with new WebSocket(url) above?

 		const onc = function(event) {
			this.statusElement.innerText = this.url + " closed " + event.code + " reason:" + event.reason;
  		}
  		this.sock.onclose = onc.bind(this);
  		
  		const cb = function(event) {
			//console.log(event.data);
			const jsondata = JSON.parse(event.data);
			if (Array.isArray(jsondata)) {
				for (let amsg of jsondata) {
					this.dispatchOne(amsg);
				}
			} else {
				this.dispatchOne(jsondata);
			}

  		}
  		this.sock.onmessage = cb.bind(this);
 	} // constructor
 	
 	dispatchOne(jsondata) {
				//console.log(mrSock.handlers.toString());
			    let one_matched = false;
				for (let handler of this.dispatch) {
					if (handler.type == jsondata.type) {
						//console.log("trying handler id: " + handler.cb + " json.type " + jsondata.type);
						handler.cb(jsondata.payload);
						one_matched = true;
						break;  // what if we allow multiple cb per "type"?  is it just that easy?
					}
				}
				if (one_matched == false) {
					console.log("unregeistered tsc event type " + jsondata.toString());
				}
    }
 	
  	send(jsn) { // better ensapsulation if we do: send(type, payload)
		this.sock.send(JSON.stringify(jsn));
	}

  	registerCallback(type, cb) {
		this.dispatch.push({type: type, cb: cb });
	}
}