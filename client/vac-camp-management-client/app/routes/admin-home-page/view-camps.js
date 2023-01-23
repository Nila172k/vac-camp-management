import Ember from 'ember';
import $ from 'jquery';

export default Ember.Route.extend({
    model() {
        var _this = this;
        $.ajax({
            url: "http://localhost:8080/vac-camps/api/v1/organizations/" + localStorage.getItem('orgId') +"/camps",
            type :"GET",
            dataType: "json",
            async: false,
            success: function(response) {
                _this.set('responseData', response)
            },
            error:(response) => {
                console.log(response);
            }
        });
        console.log( this.get('responseData') );
        return this.get('responseData')
    }
});
