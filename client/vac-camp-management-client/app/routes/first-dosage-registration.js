import Ember from 'ember';
import $ from 'jquery';

export default Ember.Route.extend({

    genericService : Ember.inject.service('generic-service'),

    beforeModel : function() {
        if(localStorage.getItem('userId') == null) {
           this.transitionTo('user-sign-in');
        }
    },

    model : function() {
        let genericService = Ember.get(this, 'genericService')
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
