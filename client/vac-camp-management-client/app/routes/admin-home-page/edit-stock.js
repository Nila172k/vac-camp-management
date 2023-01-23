import Ember from 'ember';

export default Ember.Route.extend({
    model : function(params) {
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
        var campsData = this.get('responseData').camps;      
        for(var i=0; i<campsData.length;  i++){
           var currentCamp = campsData[i];
           if(currentCamp.campId == params.camp_id){
                this.set('response', currentCamp);
               
           }
        }
        console.log(this.get('response'));
        return this.get('response');
        //return campsData ;
    }
});
