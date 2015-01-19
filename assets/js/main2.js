(function(){
    var Main = {
        img: null,
        addEvent: function(selector, eventType, func){
            var proName = "";

            switch(true){
                case /^\./.test(selector) :
                    proName = "className";
                    selector = selector.replace(".", "");
                    break;
                case /^\#/.test(selector) :
                    proName = "id";
                    selector = selector.replace("#", "");
                    break;
                default:
                    proName = "tagName";
            }

            document.body.addEventListener(eventType,function(e){
                    function check(node){
                        if(! node.parentNode) return;

                        if(node[proName] == selector){
                            func.call(node, e);
                        };
                        check(node.parentNode);
                    }
                    check(e.target);
            }, false);
        },
        eventAtt: function(){
            var _this = this;

            this.addEvent(".pic", "click", function(e){
                var text = document.getElementById("fitlerName").innerHTML;
                var img = document.getElementById("pic");
                var AP = _this.img.clone();
                if(text == "原图") AP.replace(img);
                else{

                    setTimeout(function(){
                        var t = + new Date();
                        AP.ps(text).replace(img).complete(function(){
                            console.log(text + "：" + (+ new Date() - t) / 1000 + "s");
                        });
                    }, 2);
                }
            });

            this.addEvent(".save", "click", function(e){
                console.log("1111");
                var AP = _this.img.clone();
                var img = document.getElementById("pic");
                AP.saveFileZ('demos.jpg', 0.8,img.src);
            });
        },

        init: function(){
            this.eventAtt();
            var _this = this;
            var pic = document.getElementById("pic");
            pic.onload = function(){
               _this.img = AlloyImage(this);
            };
        }
    };

    window.addEventListener("DOMContentLoaded", function(){

        //$AI.useWorker("js/combined/alloyimage.js");
        Main.init();
    }, false);

})();
