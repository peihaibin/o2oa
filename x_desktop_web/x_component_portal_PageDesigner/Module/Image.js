MWF.xApplication.portal.PageDesigner.Module.Image = MWF.PCImage = new Class({
	Extends: MWF.FCImage,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Image/image.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Image/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Image/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "image";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
