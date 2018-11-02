package com.wafersystems.notice.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/8/2 16:40 Company:
 * wafersystems
 */
@SuppressWarnings("PMD.UnusedPrivateField")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitUnReadMsg implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 499958963004770045L;
  private int unRead;
  private List<MessageToUserDto> message;
}
