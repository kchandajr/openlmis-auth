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

package org.openlmis.auth.web;

import org.openlmis.auth.i18n.MessageKeys;
import org.openlmis.auth.util.PasswordResetRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PasswordResetRequestValidator extends BaseValidator {

  private static final String USERNAME = "username";
  private static final String NEW_PASSWORD = "newPassword";
  
  @Override
  public boolean supports(Class<?> clazz) {
    return PasswordResetRequest.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    rejectIfEmptyOrWhitespace(errors, USERNAME, MessageKeys.ERROR_FIELD_REQUIRED);
    rejectIfEmptyOrWhitespace(errors, NEW_PASSWORD, MessageKeys.ERROR_FIELD_REQUIRED);
    
    if (!errors.hasErrors()) {
      PasswordResetRequest passwordResetRequest = (PasswordResetRequest) target;
      verifyPassword(passwordResetRequest.getNewPassword(), errors);
    }
  }

  private void verifyPassword(String password, Errors errors) {
    if (!password.matches("(?=.*[0-9]).+")) {
      rejectValue(errors, NEW_PASSWORD, MessageKeys.USERS_PASSWORD_RESET_NOT_CONTAIN_NUMBER);
    }
    if (!password.matches("(?=\\S+$).+")) {
      rejectValue(errors, NEW_PASSWORD, MessageKeys.USERS_PASSWORD_RESET_CONTAIN_SPACES);
    }
    if (!password.matches("^[a-zA-Z0-9]{8,16}$")) {
      rejectValue(errors, NEW_PASSWORD, MessageKeys.USERS_PASSWORD_RESET_INVALID_PASSWORD_LENGTH);
    }
  }
}