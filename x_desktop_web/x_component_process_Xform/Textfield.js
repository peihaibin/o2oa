MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xApplication.process.Xform.Textfield = MWF.APPTextfield =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "textFieldIcon",
	
	_loadUserInterface: function(){
		this._loadNode();
        if (this.json.compute == "show"){
            this._setValue(this._computeValue());
        }else{
            this._loadValue();
        }
	},
    _loadNode: function(){
        if (this.readonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    loadDescription: function(){
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if (COMMON.Browser.safari) w = w-20;
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            if (COMMON.Browser.safari){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }else if (COMMON.Browser.chrome){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }else{
                this.descriptionNode.addEvents({
                    "mousedown": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }

            this.node.getFirst().addEvents({
                "focus": function(){
                    this.descriptionNode.setStyle("display", "none");
                }.bind(this),
                "blur": function(){
                    if (!this.node.getFirst().get("value")) this.descriptionNode.setStyle("display", "block");
                }.bind(this)
            });
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
    },
    _loadNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            }
        });
        input.set(this.json.properties);

        var node = new Element("div", {"styles": {
            "overflow": "hidden",
            "position": "relative",
            "margin-right": "20px"
        }}).inject(this.node, "after");
        input.inject(node);

        this.node.destroy();
        this.node = node;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type,
            "events": {
                "click": this.clickSelect.bind(this)
            }
        });
        this.iconNode = new Element("div", {
            "styles": this.form.css[this.iconStyle]
        }).inject(this.node, "before");

        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
        //var input = new Element("input", {"styles": {
        //    "background": "transparent",
        //    "width": "100%",
        //    "border": "0px"
        //}});
        //input.set(this.json.properties);
        //
        //var node = new Element("div", {"styles": {"ovwrflow": "hidden"}}).inject(this.node, "after");
        //input.inject(node);
        //this.node.destroy();
        //this.node = node;
        //this.node.set({
			//"id": this.json.id,
			//"MWFType": this.json.type
        //});
        ////this.node.setStyle("margin-right", "18px");
        //
        //this.node.addEvent("change", function(){
        //    //if (this.json.validation){
        //    //    if (this.json.validation.code) {
        //    //        if (this.validation()) this._setBusinessData(this.getInputData("change"));
        //    //    }else{
        //    //        this._setBusinessData(this.getInputData("change"));
        //    //    }
        //    //}else{
        //        this._setBusinessData(this.getInputData("change"));
        //    //}
        //}.bind(this));
        this.node.getFirst().addEvent("blur", function(){
            this.validation();
        }.bind(this));
        this.node.getFirst().addEvent("keyup", function(){
            this.validationMode();
        }.bind(this));
	},
    getInputData: function(){
        var v = this.node.getElement("input").get("value");
        if (this.json.dataType=="number"){
            var n = v.toFloat();
            return (isNaN(n)) ? 0 : n;
        }
        return v;
    }

}); 