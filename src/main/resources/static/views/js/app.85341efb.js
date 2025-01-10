(function(){"use strict";var e={1849:function(e,t,n){var o=n(5130),i=n(6768);const s={class:"content"},r={class:"section"};function a(e,t,n,o,a,l){const c=(0,i.g2)("HeadingComponent"),d=(0,i.g2)("FileUploader"),u=(0,i.g2)("ContactForm"),h=(0,i.g2)("FooterComponent");return(0,i.uX)(),(0,i.CE)("div",s,[(0,i.Lk)("div",r,[(0,i.bF)(c,{first_line:"Subtitles corrector",second_line:"An app that fixes character encoding and formatting issues in subtitle files, ensuring they're clean and ready for use",heading_link:"https://subtitles-corrector.com"}),(0,i.bF)(d),(0,i.bF)(u)]),(0,i.bF)(h)])}var l=n(4232);const c={class:"container",style:{"margin-bottom":"24px"}},d={class:"field"},u={class:"field"};function h(e,t,n,s,r,a){const h=(0,i.g2)("GenericButton");return(0,i.uX)(),(0,i.CE)(i.FK,null,[(0,i.Lk)("div",c,[(0,i.Lk)("form",{onSubmit:t[2]||(t[2]=(0,o.D$)((e=>a.submitContactForm()),["prevent"])),class:"box",style:{"background-color":"#004266"}},[(0,i.Lk)("div",d,[t[3]||(t[3]=(0,i.Lk)("label",{class:"label has-text-white"},"Noticed a subtitle problem that Subtitles Corrector doesn't fix? Have a feedback or suggestion? Let us know!",-1)),t[4]||(t[4]=(0,i.eW)()),t[5]||(t[5]=(0,i.Lk)("br",null,null,-1)),(0,i.Lk)("input",{placeholder:"Email",class:"input",type:"email",id:"email",name:"email",onChange:t[0]||(t[0]=(...e)=>a.handleEmailChange&&a.handleEmailChange(...e)),required:""},null,32)]),(0,i.Lk)("div",u,[(0,i.Lk)("textarea",{class:"textarea",placeholder:"Description",type:"text",id:"description",name:"description",onChange:t[1]||(t[1]=(...e)=>a.handleDescriptionChange&&a.handleDescriptionChange(...e)),required:""},null,32)]),(0,i.bF)(h,{button_text:"Submit",loading:this.loading},null,8,["loading"])],32)]),""!=r.contactFormConfirmation?((0,i.uX)(),(0,i.CE)("div",{key:0,class:(0,l.C4)(["notification",this.error?"is-danger":"is-success"])},(0,l.v_)(this.contactFormConfirmation),3)):(0,i.Q3)("",!0)],64)}const p={class:"field"},f={class:"control"},b=["disabled"];function g(e,t,n,o,s,r){return(0,i.uX)(),(0,i.CE)("div",p,[(0,i.Lk)("div",f,[(0,i.Lk)("button",{type:"submit",class:(0,l.C4)(["button is-success is-fullwidth",n.loading?"is-loading":""]),disabled:n.loading},(0,l.v_)(n.button_text),11,b)])])}var m={props:{loading:Boolean,button_text:String,enabled:Boolean},name:"GenericButton",loading:!1},v=n(1241);const k=(0,v.A)(m,[["render",g]]);var C=k,y={name:"ContactForm",components:{GenericButton:C},data(){return{email:String,description:String,contactFormConfirmation:"",error:!1,loading:!1}},methods:{handleEmailChange(e){this.email=e.target.value},handleDescriptionChange(e){this.description=e.target.value},async submitContactForm(){try{this.loading=!0;const e=new FormData;e.append("email",this.email),e.append("description",this.description);const t=await fetch("api/rest/1.0/submitContactForm",{method:"POST",body:e}),n=await t.json();t.ok?(this.contactFormConfirmation="Form successfully submitted!",this.error=!1):n?(this.error=!0,this.contactFormConfirmation="FAILURE_EMAIL_SEND_RATE_LIMIT"==n?"Temporary quotas exceeded, form not submitted. Try again later.":"DEVELOPMENT_NOT_SENT"==n?"Email not sent because we are in development.":"Internal server error!"):(this.contactFormConfirmation="Internal server error!",this.error=!0)}catch(e){console.error("Error:",e),this.contactFormConfirmation=e,this.error=!0}finally{this.loading=!1}}}};const w=(0,v.A)(y,[["render",h]]);var L=w;const _={class:"file has-name is-fullwidth field"},x={class:"file-label"},F={class:"file-name",style:{"background-color":"white"}},E={key:0},S={key:0,class:"has-text-centered",style:{"margin-bottom":"24px"}},P=["href"],O={key:1,class:"box",style:{"background-color":"#004266","margin-bottom":"24px"}},D={key:0,class:"label has-text-white"},X={key:1,class:"label has-text-white"},T=["value"],I={key:2,class:"notification is-danger",style:{"margin-bottom":"24px"}};function U(e,t,n,s,r,a){const c=(0,i.g2)("GenericButton");return(0,i.uX)(),(0,i.CE)(i.FK,null,[(0,i.Lk)("form",{onSubmit:t[1]||(t[1]=(0,o.D$)(((...e)=>a.handleSubmit&&a.handleSubmit(...e)),["prevent"])),enctype:"multipart/form-data",class:"box",style:{"background-color":"#004266"}},[t[3]||(t[3]=(0,i.Lk)("label",{class:"label has-text-white"},"Upload a subtitle file (srt, sub or txt):",-1)),t[4]||(t[4]=(0,i.eW)()),t[5]||(t[5]=(0,i.Lk)("br",null,null,-1)),(0,i.Lk)("div",_,[(0,i.Lk)("label",x,[(0,i.Lk)("input",{class:"file-input",type:"file",name:"file",accept:".srt, .sub, .txt",onChange:t[0]||(t[0]=(...e)=>a.handleFileChange&&a.handleFileChange(...e))},null,32),t[2]||(t[2]=(0,i.Lk)("span",{class:"file-cta"},[(0,i.Lk)("span",{class:"file-icon"},[(0,i.Lk)("i",{class:"fas fa-upload"})]),(0,i.Lk)("span",{class:"file-label"}," Choose a file… ")],-1)),(0,i.Lk)("span",F,[this.file?((0,i.uX)(),(0,i.CE)("div",E,(0,l.v_)(this.file.name),1)):(0,i.Q3)("",!0)])])]),(0,i.bF)(c,{loading:r.loading,button_text:"Upload",enabled:this.upload_button_enabled},null,8,["loading","enabled"])],32),r.downloadLink?((0,i.uX)(),(0,i.CE)("div",S,[(0,i.Lk)("a",{href:r.downloadLink,class:"button is-link is-light"}," Download Corrected File ",8,P)])):(0,i.Q3)("",!0),Object.keys(r.fileProcessingLogs).length>0?((0,i.uX)(),(0,i.CE)("div",O,[t[6]||(t[6]=(0,i.Lk)("div",{class:"label has-text-white"}," Changes applied: ",-1)),((0,i.uX)(!0),(0,i.CE)(i.FK,null,(0,i.pI)(r.fileProcessingLogs,((e,t)=>((0,i.uX)(),(0,i.CE)("div",{class:"label",key:t},[1==e?((0,i.uX)(),(0,i.CE)("div",D,(0,l.v_)(t),1)):(0,i.Q3)("",!0),1!=e?((0,i.uX)(),(0,i.CE)("div",X,(0,l.v_)(t)+"   x"+(0,l.v_)(e),1)):(0,i.Q3)("",!0)])))),128)),(0,i.Lk)("progress",{class:"progress is-success",value:this.processedPercentage,max:"100"},(0,l.v_)(r.processedPercentage),9,T)])):(0,i.Q3)("",!0),r.error?((0,i.uX)(),(0,i.CE)("div",I,(0,l.v_)(r.error),1)):(0,i.Q3)("",!0)],64)}var A=n(46),j=n(4587),W=n.n(j),N={name:"FileUploader",components:{GenericButton:C},data(){return{file:null,loading:!1,downloadLink:"",error:null,fileProcessingLogs:{},processedPercentage:0,webSocketUserId:crypto.randomUUID(),upload_button_enabled:!0}},methods:{handleFileChange(e){this.file=e.target.files[0];const t=[".srt",".sub",".txt"],n=this.file.name.toLowerCase(),o=t.some((e=>n.endsWith(e)));o?(this.error="",this.upload_button_enabled=!0):(this.error="Invalid file type. Please upload a .srt, .sub, or .txt file.",this.upload_button_enabled=!1,e.target.value="",this.file=null)},async handleSubmit(){if(this.fileProcessingLogs={},this.processingProgress=0,!this.file)return void(this.error="Please select a file.");this.error=null,this.loading=!0,this.downloadLink="";const e=new FormData;e.append("file",this.file),e.append("webSocketUserId",this.webSocketUserId),this.fileProcessingLogs={},this.processedPercentage=0;try{const t=await fetch("api/rest/1.0/upload",{method:"POST",body:e});if(t.ok){const e=await t.text();this.downloadLink=e}else{const e=await t.text();this.error=e||"Submission failed!"}}catch(t){console.error("Error:",t),this.error="An error occurred!"}finally{this.loading=!1}},establishWSConnection(e){let t="/subtitles",n="http://localhost:8080";const o=new(W())(n+t+"/sc-ws-connection-entrypoint");this.stompClient=new A.K({webSocketFactory:()=>o,reconnectDelay:5e3,onConnect:this.onConnected,onStompError:this.onError,connectHeaders:{webSocketUserId:e}}),this.stompClient.activate()},handleMessage(e){if(e.correctionDescription){let t=this.fileProcessingLogs[e.correctionDescription];t?(t++,this.fileProcessingLogs[e.correctionDescription]=t):this.fileProcessingLogs[e.correctionDescription]=1}e.processedPercentage&&(this.processedPercentage=e.processedPercentage)},onConnected(){console.log("Connected to WebSocket");const e=/.*\/(.*)\/websocket/;let t="";try{t=e.exec(this.stompClient.webSocket._transport.url)[1]}catch(n){console.error(n),console.log(this.stompClient.webSocket._transport.url)}this.stompClient.subscribe("/user/"+t+"/subtitles-processing-log",(e=>{this.handleMessage(JSON.parse(e.body))}))},onError(e){console.error("WebSocket error:",e)},sendMessage(e){this.stompClient&&this.stompClient.connected&&this.stompClient.publish({destination:"/app/ws/1.0/upload",body:JSON.stringify({message:e})})}},mounted:function(){this.establishWSConnection(this.webSocketUserId)}};const Q=(0,v.A)(N,[["render",U]]);var B=Q;const M={class:"footer"};function G(e,t,n,o,s,r){return(0,i.uX)(),(0,i.CE)("footer",M,t[0]||(t[0]=[(0,i.Lk)("div",{class:"content has-text-centered"},[(0,i.Lk)("p",null,[(0,i.eW)(" The source code & website content is licensed "),(0,i.Lk)("a",{href:"https://creativecommons.org/licenses/by-nc/4.0/deed.en"},"CC BY-NC 4.0"),(0,i.eW)(". ")])],-1)]))}var H={name:"FooterComponent"};const K=(0,v.A)(H,[["render",G]]);var q=K;const J=["href"],R={key:0,class:"title has-text-primary has-text-centered"},$={key:0,class:"subtitle has-text-centered"};function V(e,t,n,o,s,r){return(0,i.uX)(),(0,i.CE)(i.FK,null,[(0,i.Lk)("a",{href:this.heading_link},[n.first_line?((0,i.uX)(),(0,i.CE)("h1",R,(0,l.v_)(n.first_line),1)):(0,i.Q3)("",!0),t[0]||(t[0]=(0,i.eW)()),t[1]||(t[1]=(0,i.Lk)("br",null,null,-1))],8,J),n.second_line?((0,i.uX)(),(0,i.CE)("p",$,(0,l.v_)(n.second_line),1)):(0,i.Q3)("",!0)],64)}var Y={name:"HeadingComponent",props:{first_line:String,second_line:String,heading_link:String}};const z=(0,v.A)(Y,[["render",V]]);var Z=z,ee={name:"App",components:{FileUploader:B,FooterComponent:q,HeadingComponent:Z,ContactForm:L},mounted:async function(){const e=await fetch("https://hutils.loxal.net/whois",{method:"GET"});if(e.ok){const t=await e.json();fetch("api/rest/1.0/logUser",{method:"POST",body:JSON.stringify(t),headers:new Headers({"Content-Type":"application/json"})})}}};const te=(0,v.A)(ee,[["render",a]]);var ne=te;(0,o.Ef)(ne).mount("#app")}},t={};function n(o){var i=t[o];if(void 0!==i)return i.exports;var s=t[o]={exports:{}};return e[o].call(s.exports,s,s.exports,n),s.exports}n.m=e,function(){var e=[];n.O=function(t,o,i,s){if(!o){var r=1/0;for(d=0;d<e.length;d++){o=e[d][0],i=e[d][1],s=e[d][2];for(var a=!0,l=0;l<o.length;l++)(!1&s||r>=s)&&Object.keys(n.O).every((function(e){return n.O[e](o[l])}))?o.splice(l--,1):(a=!1,s<r&&(r=s));if(a){e.splice(d--,1);var c=i();void 0!==c&&(t=c)}}return t}s=s||0;for(var d=e.length;d>0&&e[d-1][2]>s;d--)e[d]=e[d-1];e[d]=[o,i,s]}}(),function(){n.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return n.d(t,{a:t}),t}}(),function(){n.d=function(e,t){for(var o in t)n.o(t,o)&&!n.o(e,o)&&Object.defineProperty(e,o,{enumerable:!0,get:t[o]})}}(),function(){n.g=function(){if("object"===typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"===typeof window)return window}}()}(),function(){n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)}}(),function(){var e={524:0};n.O.j=function(t){return 0===e[t]};var t=function(t,o){var i,s,r=o[0],a=o[1],l=o[2],c=0;if(r.some((function(t){return 0!==e[t]}))){for(i in a)n.o(a,i)&&(n.m[i]=a[i]);if(l)var d=l(n)}for(t&&t(o);c<r.length;c++)s=r[c],n.o(e,s)&&e[s]&&e[s][0](),e[s]=0;return n.O(d)},o=self["webpackChunkfrontend_vue"]=self["webpackChunkfrontend_vue"]||[];o.forEach(t.bind(null,0)),o.push=t.bind(null,o.push.bind(o))}();var o=n.O(void 0,[504],(function(){return n(1849)}));o=n.O(o)})();
//# sourceMappingURL=app.85341efb.js.map