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

package com.pentaho.profiling.api.action;

import com.pentaho.profiling.api.ProfileStatusMessage;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by bryan on 8/11/14.
 */
public class DefaultProfileActionTest {
  @Test
  public void testDefaultConstructorNullThen() {
    DefaultProfileAction defaultProfileAction = new DefaultProfileAction() {
      @Override public ProfileActionResult execute() {
        return null;
      }

      @Override public ProfileStatusMessage getCurrentOperation() {
        return null;
      }
    };
    assertEquals( null, defaultProfileAction.then() );
  }

  @Test(expected = ThenAlreadyRequestedException.class)
  public void testExceptionThrownIfThenSetAfterRequested() throws ThenAlreadyRequestedException {
    DefaultProfileAction defaultProfileAction = new DefaultProfileAction() {
      @Override public ProfileActionResult execute() {
        return null;
      }

      @Override public ProfileStatusMessage getCurrentOperation() {
        return null;
      }
    };
    defaultProfileAction.then();
    defaultProfileAction.setThen( null );
  }

  @Test
  public void testSetThen() throws ThenAlreadyRequestedException {
    ProfileAction then = mock( ProfileAction.class );
    DefaultProfileAction defaultProfileAction = new DefaultProfileAction() {
      @Override public ProfileActionResult execute() {
        return null;
      }

      @Override public ProfileStatusMessage getCurrentOperation() {
        return null;
      }
    };
    defaultProfileAction.setThen( then );
    assertEquals( then, defaultProfileAction.then() );
  }

  @Test
  public void testStop() {
    AtomicBoolean stopped = new AtomicBoolean( false );
    ProfileAction then = mock( ProfileAction.class );
    DefaultProfileAction defaultProfileAction = new DefaultProfileAction( then, stopped ) {
      @Override public ProfileActionResult execute() {
        return null;
      }

      @Override public ProfileStatusMessage getCurrentOperation() {
        return null;
      }
    };
    assertTrue( stopped == defaultProfileAction.getStopped() );
    defaultProfileAction.stop();
    assertNull( defaultProfileAction.then() );
  }
}
