import Ember from 'ember';

export default Ember.Route.extend({
    model : function() {
        var _this = this;
        $.ajax({
            url : "http://localhost:8080/vac-camps/api/v1/cities",
            type: "GET",
            dataType:"json",
            async: false,
            success : function(data) {
                var cityData = data;
                _this.set("modelData", JSON.parse(cityData.cities));
            },
            error : (response) => {
                 alert(JSON.stringify(response).length);
            }
        })
        return this.get("modelData");
    }
});
