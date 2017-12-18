/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.auth.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.auth.OAuth2AuthenticationDataBuilder.SERVICE_CLIENT_ID;
import static org.openlmis.auth.service.PermissionService.USERS_MANAGE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.auth.DummyRightDto;
import org.openlmis.auth.DummyUserDto;
import org.openlmis.auth.OAuth2AuthenticationDataBuilder;
import org.openlmis.auth.dto.ResultDto;
import org.openlmis.auth.dto.RightDto;
import org.openlmis.auth.dto.referencedata.UserDto;
import org.openlmis.auth.exception.PermissionMessageException;
import org.openlmis.auth.service.referencedata.UserReferenceDataService;
import org.openlmis.auth.util.AuthenticationHelper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {

  @Mock
  private AuthenticationHelper authenticationHelper;

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @InjectMocks
  private PermissionService permissionService;

  private SecurityContext securityContext;

  private OAuth2Authentication trustedClient;
  private OAuth2Authentication userClient;
  private OAuth2Authentication apiKeyClient;

  private UserDto user = new DummyUserDto();
  private RightDto right = new DummyRightDto();

  @Before
  public void setUp() {
    securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    trustedClient = new OAuth2AuthenticationDataBuilder().buildServiceAuthentication();
    userClient = new OAuth2AuthenticationDataBuilder().buildUserAuthentication();
    apiKeyClient = new OAuth2AuthenticationDataBuilder().buildApiKeyAuthentication();

    when(authenticationHelper.getCurrentUser()).thenReturn(user);
    when(authenticationHelper.getRight(USERS_MANAGE)).thenReturn(right);
    when(userReferenceDataService.hasRight(user.getId(), right.getId(), null, null, null))
        .thenReturn(new ResultDto<>(true));

    ReflectionTestUtils.setField(permissionService, "serviceTokenClientId", SERVICE_CLIENT_ID);
  }

  @Test
  public void userShouldBeAbleToManageUsers() {
    when(securityContext.getAuthentication()).thenReturn(userClient);
    permissionService.canManageUsers();
  }

  @Test(expected = PermissionMessageException.class)
  public void userShouldNotBeAbleToManageUsersIfHasNoRight() {
    when(securityContext.getAuthentication()).thenReturn(userClient);
    when(userReferenceDataService.hasRight(user.getId(), right.getId(), null, null, null))
        .thenReturn(new ResultDto<>(false));

    permissionService.canManageUsers();
  }

  @Test(expected = PermissionMessageException.class)
  public void serviceShouldNotBeAbleToManageUsers() {
    when(securityContext.getAuthentication()).thenReturn(trustedClient);
    permissionService.canManageUsers();
  }

  @Test(expected = PermissionMessageException.class)
  public void apiKeyShouldNotBeAbleToManageUsers() {
    when(securityContext.getAuthentication()).thenReturn(apiKeyClient);
    permissionService.canManageUsers();
  }

  @Test(expected = PermissionMessageException.class)
  public void userShouldNotBeAbleToManageApiKeys() {
    when(securityContext.getAuthentication()).thenReturn(userClient);
    permissionService.canManageApiKeys();
  }

  @Test
  public void serviceShouldBeAbleToManageApiKeys() {
    when(securityContext.getAuthentication()).thenReturn(trustedClient);
    permissionService.canManageApiKeys();
  }

  @Test(expected = PermissionMessageException.class)
  public void apiKeyShouldNotBeAbleToManageApiKeys() {
    when(securityContext.getAuthentication()).thenReturn(apiKeyClient);
    permissionService.canManageApiKeys();
  }
}