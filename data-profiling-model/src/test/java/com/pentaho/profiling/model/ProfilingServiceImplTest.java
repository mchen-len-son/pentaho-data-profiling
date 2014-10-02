/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.profiling.model;

import com.pentaho.profiling.api.MutableProfileStatus;
import com.pentaho.profiling.api.Profile;
import com.pentaho.profiling.api.ProfileCreationException;
import com.pentaho.profiling.api.ProfileFactory;
import com.pentaho.profiling.api.ProfileStatus;
import com.pentaho.profiling.api.ProfileStatusManager;
import com.pentaho.profiling.api.ProfileStatusReadOperation;
import com.pentaho.profiling.api.ProfileStatusWriteOperation;
import com.pentaho.profiling.api.datasource.DataSourceReference;
import com.pentaho.profiling.api.operations.ProfileOperation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.osgi.notification.api.NotificationListener;
import org.pentaho.osgi.notification.api.NotificationObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/14/14.
 */
public class ProfilingServiceImplTest {
  private ProfileFactory profileFactory;
  private ProfilingServiceImpl profilingService;
  private Profile profile;
  private ProfileStatus profileStatus;
  private ProfileStatusManager profileStatusManager;
  private String profileId;

  @Before
  public void setup() {
    profileFactory = mock( ProfileFactory.class );
    profilingService = new ProfilingServiceImpl();
    profilingService.setFactories( Arrays.asList( profileFactory ) );
    profile = mock( Profile.class );
    profileId = "test-id";
    when( profile.getId() ).thenReturn( profileId );
    profileStatusManager = mock( ProfileStatusManager.class );
    profileStatus = mock( ProfileStatus.class );
    when( profileStatus.getId() ).thenReturn( profileId );
  }

  @Test
  public void testSetFactories() {
    ProfilingServiceImpl profilingService = new ProfilingServiceImpl();
    List<ProfileFactory> profileFactories = new ArrayList<ProfileFactory>();
    profileFactories.add( profileFactory );
    profilingService.setFactories( profileFactories );
    assertEquals( profileFactories, profilingService.getFactories() );
  }

  @Test
  public void testCreateNoFactories() throws ProfileCreationException {
    profilingService.setFactories( new ArrayList<ProfileFactory>() );
    assertNull( profilingService.create( new DataSourceReference( "Test", "Test" ) ) );
  }

  @Test
  public void testCreateNoMatchingFactories() throws ProfileCreationException {
    DataSourceReference dataSourceReference = new DataSourceReference();
    when( profileFactory.accepts( dataSourceReference ) ).thenReturn( false );
    assertNull( profilingService.create( dataSourceReference ) );
  }

  @Test
  public void testCreateMatchingFactory() throws ProfileCreationException {
    DataSourceReference dataSourceReference = new DataSourceReference();
    ProfileStatus profileStatus = mock( ProfileStatusImpl.class );
    when( profileFactory.accepts( dataSourceReference ) ).thenReturn( true );
    when( profileFactory.create( any( ProfileStatusManager.class ) ) ).thenReturn( profile );
    ProfileStatusManager profileStatusManager = profilingService.create( dataSourceReference );
    assertEquals( dataSourceReference, profileStatusManager.getDataSourceReference() );
  }

  @Test
  public void testGetActiveProfiles() {
    String profileId = "PROFILE_ID";
    ProfileStatus profileStatus = mock( ProfileStatusImpl.class );
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.getProfileStatusManagerMap().put( profileId, profileStatusManager );
    List<ProfileStatusManager> statuses = profilingService.getActiveProfiles();
    assertEquals( 1, statuses.size() );
    assertEquals( profileStatusManager, statuses.get( 0 ) );
  }

  @Test
  public void testGetProfileUpdate() {
    String profileId = "PROFILE_ID";
    profilingService.getProfileStatusManagerMap().put( profileId, profileStatusManager );
    assertEquals( profileStatusManager, profilingService.getProfileUpdate( profileId ) );
  }

  @Test
  public void testStop() {
    String profileId = "PROFILE_ID";
    ProfileStatus profileStatus = mock( ProfileStatusImpl.class );
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.stopCurrentOperation( profileId );
    verify( profile ).stopCurrentOperation();
  }

  @Test
  public void testStart() {
    String profileId = "PROFILE_ID";
    String operationId = "OPERATION_ID";
    ProfileStatus profileStatus = mock( ProfileStatusImpl.class );
    profilingService.getProfileMap().put( profileId, profile );
    profilingService.startOperation( profileId, operationId );
    verify( profile ).startOperation( operationId );
  }

  @Test
  public void testGetOperations() {
    String profileId = "PROFILE_ID";
    ProfileOperation profileOperation = mock( ProfileOperation.class );
    List<ProfileOperation> profileOperations = new ArrayList<ProfileOperation>( Arrays.asList( profileOperation ) );
    when( profile.getProfileOperations() ).thenReturn( profileOperations );
    profilingService.getProfileMap().put( profileId, profile );
    assertEquals( profileOperations, profilingService.getOperations( profileId ) );
  }

  @Test
  public void testDiscard() {
    when( profileStatusManager.getId() ).thenReturn( profileId );
    profilingService.getProfileStatusManagerMap().put( profileId, profileStatusManager );
    assertEquals( profileStatusManager, profilingService.getProfileUpdate( profileId ) );
    profilingService.discardProfile( profileId );
    assertNull( profilingService.getProfileUpdate( profileId ) );
  }

  @Test
  public void testGetEmittedTypes() {
    assertEquals( new HashSet<String>( Arrays.asList( ProfilingServiceImpl.class.getCanonicalName() ) ),
      profilingService.getEmittedTypes() );
  }

  @Test
  public void testRegister() {
    NotificationListener notificationListener = mock( NotificationListener.class );
    profilingService.register( notificationListener );
    ArgumentCaptor<NotificationObject> argumentCaptor = ArgumentCaptor.forClass( NotificationObject.class );
    profilingService.notify( profileStatus );
    verify( notificationListener ).notify( argumentCaptor.capture() );
    NotificationObject notificationObject = argumentCaptor.getValue();
    assertEquals( profileId, notificationObject.getId() );
    assertEquals( ProfilingServiceImpl.class.getCanonicalName(), notificationObject.getType() );
    assertEquals( profileStatus, notificationObject.getObject() );
  }

  @Test
  public void testUnRegister() {
    NotificationListener notificationListener = mock( NotificationListener.class );
    profilingService.register( notificationListener );
    profilingService.unregister( notificationListener );
    profilingService.notify( profileStatus );
    verifyNoMoreInteractions( notificationListener );
  }

  @Test
  public void testPreviousNotifications() {
    when( profileStatusManager.read( any( ProfileStatusReadOperation.class ) ) ).thenAnswer( new Answer<Object>() {
      @Override public Object answer( InvocationOnMock invocation ) throws Throwable {
        return ( (ProfileStatusReadOperation) invocation.getArguments()[0]).read( profileStatus );
      }
    } );
    profilingService.notify( profileStatus );
    ArgumentCaptor<NotificationObject> argumentCaptor = ArgumentCaptor.forClass( NotificationObject.class );
    NotificationListener notificationListener = mock( NotificationListener.class );
    profilingService.register( notificationListener );
    verify( notificationListener ).notify( argumentCaptor.capture() );
    NotificationObject notificationObject = argumentCaptor.getValue();
    assertEquals( profileId, notificationObject.getId() );
    assertEquals( ProfilingServiceImpl.class.getCanonicalName(), notificationObject.getType() );
    assertEquals( profileStatus, notificationObject.getObject() );
  }
}
