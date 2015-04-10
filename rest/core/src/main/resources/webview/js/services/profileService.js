/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002  2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and nondisclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

define(['./services'], function (appServices) {
  appServices.factory('ProfileService', ['$resource',
    function ($resource) {
      var profileResource = $resource('../cxf/profile/:profileId', {}, {
        getActiveProfiles: {method: 'GET', url: '../cxf/profile', isArray: true},
        getProfile: {method: 'GET'},
        stopProfile: {method: 'GET', url: '../cxf/profile/stop/:profileId'},
        startProfile: {method: 'PUT', url: '../cxf/profile/start'}
      });
      var aggregateProfileResource = $resource('../cxf/aggregate/:profileId', {}, {
        getAggregates: {method: 'GET', url: '../cxf/aggregate', isArray: true},
        getAggregate: {method: 'GET', params: {profileId: 'profileId'}, isArray: true}
      });
      var metricContributorResource = $resource('../cxf/metrics', {}, {
        getDefaultMetricContributorConfig: {method: 'GET', url: '../cxf/metrics/default'},
        setDefaultMetricContributorConfig: {method: 'POST', url: '../cxf/metrics/default'},
        getAllAvailableMetricContributorConfig: {method: 'GET', url: '../cxf/metrics/full'}
      });

      function ProfileService() {
      }

      ProfileService.prototype = {
        constructor: ProfileService,
        aggregateProfileResource: aggregateProfileResource,
        profileResource: profileResource,
        metricContributorResource: metricContributorResource
      };
      var profileService = new ProfileService();
      return profileService;
    }])
});