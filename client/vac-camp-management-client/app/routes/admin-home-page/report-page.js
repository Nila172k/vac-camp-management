import Ember from 'ember';
import $ from 'jquery';

export default Ember.Route.extend({
    model : function() {
        var _this =  this;
        $.ajax({
            url  : "http://localhost:8080/vac-camps/api/v1/organizations/"+ localStorage.getItem('orgId') +"/summaries",
            type :"GET",
            dataType: "json",
            async: false,
            success: function(response){
                //console.log( JSON.parse(response[0]).City );
                _this.set('modelData', response)
            },
            error:(response) => {
                console.log(response);
            }
        });
        console.log(_this.get('modelData'));
        return _this.get('modelData');
    }
});
