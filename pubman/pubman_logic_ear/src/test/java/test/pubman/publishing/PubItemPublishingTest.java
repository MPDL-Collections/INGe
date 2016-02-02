/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package test.pubman.publishing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.pubman.TestBase;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.pubman.PubItemPublishing;
import de.mpg.escidoc.services.pubman.exceptions.PubItemStatusInvalidException;

/**
 * Test class for {@link PubItemPublishing}
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ Revised by StG: 24.08.2007
 */
public class PubItemPublishingTest extends TestBase {
  private static final String HANDLE_PREFIX = "hdl:";
  private static PubItemPublishing pmPublishing;
  private static PubItemDepositing pmDepositing;

  private AccountUserVO user;
  private AccountUserVO otherUser;

  /**
   * The setUp method for setting the needed values of the variables used in that test.
   * 
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    pmPublishing =
        (PubItemPublishing) getService("ejb:pubman_logic_ear/pubman_logic/PubItemPublishingBean!"
            + PubItemPublishing.class.getName());
    pmDepositing =
        (PubItemDepositing) getService("ejb:pubman_logic_ear/pubman_logic/PubItemDepositingBean!"
            + PubItemDepositing.class.getName());
    otherUser = getUserTestDepScientistWithHandle();
    user = getUserTestDepLibWithHandle();
  }

  /**
   * Test of {@link PubItemPublishing#releasePubItem(ItemRO, java.util.Date, AccountUserVO)}
   * 
   * @throws Exception
   */
  @Test
  public void testReleasePubItem() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    PubItemVO savedItem = pmDepositing.savePubItem(item, user);
    ItemRO pubItemRef =
        pmDepositing.submitAndReleasePubItem(savedItem, "Test Submit", user).getVersion();
    assertNotNull(pubItemRef);

    addForDeletion(pubItemRef.getObjectId());

    Thread.sleep(3000);

    // retrieve item to verify state
    PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
    assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());

    // object pid and version pid
    assertTrue(releasedPubItem.getPid().startsWith(HANDLE_PREFIX));
    assertTrue(releasedPubItem.getVersion().getPid().startsWith(HANDLE_PREFIX));
  }


  /**
   * Test of {@link PubItemPublishing#releasePubItem(ItemRO, java.util.Date, AccountUserVO)}
   * 
   * @throws Exception
   */
  @Test
  public void testReleasePubItemWithFile() throws Exception {
    // new item
    PubItemVO item = getNewPubItemWithoutFiles();

    // Add file to item
    FileVO file = new FileVO();
    String testfile = "src/test/resources/publishing/pubItemPublishingTest/farbtest.gif";
    file.setContent(uploadFile(testfile, "image/gif", user.getHandle()).toString());
    file.setMimeType("image/gif");
    file.setContentCategory("http://purl.org/escidoc/metadata/ves/content-categories/publisher-version");
    file.setVisibility(Visibility.PUBLIC);

    MdsFileVO mdsFileVO = new MdsFileVO();
    file.setDefaultMetadata(mdsFileVO);
    file.getDefaultMetadata().setTitle(new TextVO("farbtest.gif"));
    file.getDefaultMetadata().setDescription("Ein Farbtest.");
    FormatVO formatVO = new FormatVO();
    formatVO.setType("dcterms:IMT");
    formatVO.setValue("image/gif");
    file.getDefaultMetadata().getFormats().add(formatVO);

    file.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    item.getFiles().add(file);

    // Add external url to item
    FileVO extUrl = new FileVO();

    extUrl.setContent("http://google.de");
    extUrl.setName("Google Link");
    extUrl
        .setContentCategory("http://purl.org/escidoc/metadata/ves/content-categories/publisher-version");
    extUrl.setVisibility(Visibility.PUBLIC);

    MdsFileVO metadata = new MdsFileVO();
    metadata
        .setContentCategory("http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext");
    metadata.setTitle(new TextVO("Link"));
    extUrl.getMetadataSets().add(metadata);

    extUrl.setStorage(FileVO.Storage.EXTERNAL_URL);
    item.getFiles().add(extUrl);

    PubItemVO savedItem = pmDepositing.savePubItem(item, user);
    ItemRO pubItemRef =
        pmDepositing.submitAndReleasePubItem(savedItem, "Test Submit", user).getVersion();
    assertNotNull(pubItemRef);

    addForDeletion(pubItemRef.getObjectId());

    Thread.sleep(3000);

    // retrieve item to verify state
    PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
    assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());
    assertNotNull(releasedPubItem.getPid());

    assertEquals(2, releasedPubItem.getFiles().size());

    for (FileVO pubFile : releasedPubItem.getFiles()) {
      if (pubFile.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
        assertTrue(pubFile.getPid().startsWith(HANDLE_PREFIX));
      } else {
        assertTrue(pubFile.getPid() == null || "".equals(pubFile.getPid()));
      }
    }
  }

  /**
   * Test of {@link PubItemPublishing#releasePubItem(ItemRO, java.util.Date, AccountUserVO)} with
   * invald state.
   * 
   * @throws Exception
   */
  @Test
  public void testReleasePendingPubItem() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    PubItemVO pubItemSaved = pmDepositing.savePubItem(item, user);
    assertNotNull(pubItemSaved);

    addForDeletion(pubItemSaved.getLatestVersion().getObjectId());

    try {
      pmPublishing.releasePubItem(pubItemSaved.getVersion(), pubItemSaved.getModificationDate(),
          "Test Release", user);
      fail("Exception expected!!!");

    } catch (PubItemStatusInvalidException e) {

    }
  }

  /**
   * Test of
   * {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)}
   * 
   * @throws Exception
   */
  @Ignore("Expected error not clear.")
  @Test(expected = ItemNotFoundException.class)
  public void testWithdrawPubItem() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
    assertNotNull(pubItemRef);

    addForDeletion(pubItemRef.getObjectId());

    // retrieve item
    PubItemVO submittedPubItem = getPubItemFromFramework(pubItemRef, user);
    assertNotNull(submittedPubItem);

    // release not necessary, is included in submit
    assertEquals(PubItemVO.State.RELEASED, submittedPubItem.getVersion().getState());

    pmPublishing.withdrawPubItem(submittedPubItem, submittedPubItem.getModificationDate(),
        "That is why", user);

    // retrieve item to verify state
    try {
      PubItemVO withdrawnPubItem = getPubItemFromFramework(pubItemRef, user);
      // The following code cannot be reached, because an exception will be thrown before. Reason:
      // Withdrawn items cannot be retrieved (by objectId)
      // TODO FRM: Check whether this behaviour of the framework is okay.
      assertEquals(PubItemVO.State.WITHDRAWN, withdrawnPubItem.getPublicStatus());
      assertEquals("That is why", withdrawnPubItem.getWithdrawalComment());
    } catch (ItemNotFoundException e) {
      assertTrue(e.getFaultString().indexOf("Item is found but withdrawn.") > -1);
      throw e;
    }

  }

  /**
   * Test of
   * {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)} with
   * valid state.
   * 
   * @throws Exception Any exception.
   */
  @Test
  public final void testWithdrawReleasedPubItem() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    PubItemVO savedItem = pmDepositing.savePubItem(item, user);
    ItemRO pubItemRef =
        pmDepositing.submitAndReleasePubItem(savedItem, "Test Submit", user).getVersion();
    assertNotNull(pubItemRef);

    addForDeletion(pubItemRef.getObjectId());

    Thread.sleep(3000);

    // retrieve item to verify state
    PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
    assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());

    pmPublishing.withdrawPubItem(releasedPubItem, releasedPubItem.getModificationDate(),
        "Dies ist dör withdrawal comment", user); // "Dies ist dör „withdrawal comment“"

    PubItemVO withdrawnPubItem = getPubItemFromFramework(pubItemRef, user);
    assertNotNull(withdrawnPubItem);
    assertEquals(PubItemVO.State.WITHDRAWN, withdrawnPubItem.getPublicStatus());
  }

  /**
   * Test of
   * {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)} with
   * valid state.
   * 
   * @throws Exception Any exception exept a security exception.
   */
  @Test
  public final void testWithdrawReleasedPubItemWithAdminUser() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    PubItemVO savedItem = pmDepositing.savePubItem(item, user);
    ItemRO pubItemRef =
        pmDepositing.submitAndReleasePubItem(savedItem, "Test Submit", user).getVersion();
    assertNotNull(pubItemRef);

    addForDeletion(pubItemRef.getObjectId());

    Thread.sleep(3000);

    // retrieve item to verify state
    PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
    assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());
    String withdrawalComment = "Dies ist dör withdrawal comment"; // "Dies ist dör „withdrawal comment“"

    pmPublishing.withdrawPubItem(releasedPubItem, releasedPubItem.getModificationDate(),
        withdrawalComment, getUserSystemAdministratorWithHandle());

    PubItemVO withdrawnPubItem = getPubItemFromFramework(pubItemRef, user);
    assertNotNull(withdrawnPubItem);
    assertEquals(PubItemVO.State.WITHDRAWN, withdrawnPubItem.getPublicStatus());
    // assertEquals(withdrawnPubItem.getCurrentVersion().getComment(), withdrawalComment);
  }

  /**
   * Test of
   * {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)} with
   * valid state.
   * 
   * @throws Exception Any exception exept a security exception.
   */
  @Ignore("At the moment, no user exists in the framework who hasn't the rights to withdraw an item.")
  @Test(expected = SecurityException.class)
  public final void testWithdrawReleasedPubItemWithWrongUser() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    ItemRO pubItemRef = pmDepositing.submitPubItem(item, "Test Submit", user).getVersion();
    assertNotNull(pubItemRef);

    addForDeletion(pubItemRef.getObjectId());

    Thread.sleep(3000);

    // retrieve item to verify state
    PubItemVO releasedPubItem = getPubItemFromFramework(pubItemRef, user);
    assertEquals(PubItemVO.State.RELEASED, releasedPubItem.getVersion().getState());
    String withdrawalComment = "Dies ist dör withdrawal comment"; // "Dies ist dör „withdrawal comment“"
    // Here, a SecurityExeption should be thrown
    pmPublishing.withdrawPubItem(releasedPubItem, releasedPubItem.getModificationDate(),
        withdrawalComment, otherUser);

  }

  /**
   * Test of
   * {@link PubItemPublishing#withdrawPubItem(ItemRO, java.util.Date, String, AccountUserVO)} with
   * invalid state.
   * 
   * @throws Exception
   */
  @Test(expected = PubItemStatusInvalidException.class)
  public void testWithdrawPendingPubItem() throws Exception {
    // create pubItem to get Reference
    PubItemVO item = getNewPubItemWithoutFiles();
    item.setVersion(null);
    PubItemVO pubItemSaved = pmDepositing.savePubItem(item, user);
    assertNotNull(pubItemSaved);

    addForDeletion(pubItemSaved.getLatestVersion().getObjectId());

    pmPublishing.withdrawPubItem(pubItemSaved, pubItemSaved.getModificationDate(), "That is why",
        user);
    fail("Exception expected!!!");

  }
}
