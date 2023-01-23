import Ember from 'ember';

export default Ember.Service.extend({
    orgId :null,
    orgType : null,
    getOrgId() {
        return this.get('orgId');
    },
    setOrgId(newOrgId) {
        this.set('orgId', newOrgId);
    },
    getOrgType() {
        return this.get('orgType')
    },
    setOrgType(newOrgType) {
        this.get('orgType', newOrgType);
    }
});
