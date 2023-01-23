import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('user-sign-up');
  this.route('user-sign-in');
  this.route('first-dosage-registration');
  this.route('second-dosage-registration');
  this.route('org-login');
  this.route('org-home-page');
  this.route('admin-sign-in');
  this.route('admin-home-page', function() {
    this.route('report-page');
    this.route('new-camp');
    //this.route('edit-stock');
    this.route('edit-stock', {path: '/camps/:camp_id/edit-stock'});
    this.route('view-camps');
  });
  this.route('users', function() {
    this.route('sign-in');
    this.route('sign-up');
    this.route('home-page');
  });
  this.route('admins', function() {
    this.route('report-page');
    this.route('sign-in');
    this.route('register-camp');
    this.route('list-camps', function() {
      this.route('edit-stock', {path: '/camps/:camp_id/edit-stock'});
    });
  });
});

export default Router;
