package shop.main;

import junit.framework.TestCase;
import shop.command.RerunnableCommand;
import shop.command.UndoableCommand;
import shop.data.Data;
import shop.data.Video;
import shop.data.Inventory;

public class TEST2 extends TestCase {
  public TEST2(String name) {
    super(name);
  }
  public void test1() {
    final Inventory inventory = Data.newInventory();
    final Video v1 = Data.newVideo("K", 2003, "S");
    final Video v2 = Data.newVideo("S", 2002, "K");
    final RerunnableCommand UNDO = Data.newUndoCmd(inventory);
    final RerunnableCommand REDO = Data.newRedoCmd(inventory);
	
    UndoableCommand c = Data.newAddCmd(inventory, v1, 2);
    assertTrue  ( c.run() );
    assertEquals( 1, inventory.size() );
    
    assertFalse  (c.run() );     // cannot run an undoable command twice
    assertFalse  (Data.newAddCmd(inventory, null, 3).run()); // can't add null!
    assertFalse  (Data.newAddCmd(inventory, v2, 0).run());   // can't add zero copies!
   
    assertEquals( 1, inventory.size() );
    assertTrue  ( UNDO.run() );
    assertEquals( 0, inventory.size() );
    
    assertFalse  (UNDO.run() );  // nothing to undo!
    assertEquals( 0, inventory.size() );
    
    assertTrue  ( REDO.run() );
    assertEquals( 1, inventory.size() );
    
    assertFalse  (REDO.run() );  // nothing to redo!
    assertEquals( 1, inventory.size() );
    
    assertTrue  ( Data.newAddCmd(inventory, v1, -2).run());   // delete!
    assertEquals( 0, inventory.size() );
    
    assertFalse  (Data.newOutCmd(inventory, v1).run());       // can't check out
    assertEquals( 0, inventory.size() );
    
    assertTrue  ( UNDO.run() );  // should undo the AddCmd, not the OutCmd
    assertEquals( 1, inventory.size() ); 
    assertFalse  (Data.newAddCmd(inventory, v1, -3).run());   // can't delete 3
    
    assertTrue  ( Data.newAddCmd(inventory, v1, -2).run());   // delete 2
    assertEquals( 0, inventory.size() );
    
    assertTrue  ( UNDO.run() ); 
    assertEquals( 1, inventory.size() ); 
    assertEquals( "K (2003) : S [2,0,0]", inventory.get(v1).toString() );	
    
    // add 2 more copies of v1
    assertTrue  ( Data.newAddCmd(inventory, v1, 2).run()); 
    assertEquals( "K (2003) : S [4,0,0]", inventory.get(v1).toString() );	
    
    // add 2 more copies of v1
    assertTrue  ( Data.newAddCmd(inventory, v1, 2).run());
    assertEquals( "K (2003) : S [6,0,0]", inventory.get(v1).toString() );	
    
    //undo add, down to 4 copies
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [4,0,0]", inventory.get(v1).toString() );	
    
    //undo add, down to 2 copies
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,0,0]", inventory.get(v1).toString() );	

    //check out v1, increment numOut, Rentals
    assertTrue  ( Data.newOutCmd(inventory, v1).run());
    assertEquals( "K (2003) : S [2,1,1]", inventory.get(v1).toString() );
    
    //check out v1, increment numOut, Rentals
    assertTrue  ( Data.newOutCmd(inventory, v1).run());
    assertEquals( "K (2003) : S [2,2,2]", inventory.get(v1).toString() );
    
    //Attempt to check out v1, but numOut == numOwned
    assertFalse  (Data.newOutCmd(inventory, v1).run());
    assertEquals( "K (2003) : S [2,2,2]", inventory.get(v1).toString() );
    
    // undo check out, decrement numOut, numRentals
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,1,1]", inventory.get(v1).toString() );
    
    // undo check out, decrement numOut, numRentals
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,0,0]", inventory.get(v1).toString() );
    
    // fails
    assertTrue  ( REDO.run() );
    assertEquals( "K (2003) : S [2,1,1]", inventory.get(v1).toString() );
    assertTrue  ( REDO.run() );
    assertEquals( "K (2003) : S [2,2,2]", inventory.get(v1).toString() );
    
    assertTrue  ( Data.newInCmd(inventory, v1).run() );
    assertEquals( "K (2003) : S [2,1,2]", inventory.get(v1).toString() );	
    assertTrue  ( Data.newInCmd(inventory, v1).run() );
    assertEquals( "K (2003) : S [2,0,2]", inventory.get(v1).toString() );
    assertFalse  (
    		Data.newInCmd(inventory, v1).run() );
    assertEquals( "K (2003) : S [2,0,2]", inventory.get(v1).toString() );
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,1,2]", inventory.get(v1).toString() );
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,2,2]", inventory.get(v1).toString() );
    assertTrue  ( REDO.run() );
    assertEquals( "K (2003) : S [2,1,2]", inventory.get(v1).toString() );
    assertTrue  ( REDO.run() );
    assertEquals( "K (2003) : S [2,0,2]", inventory.get(v1).toString() );
    
    assertTrue  ( Data.newAddCmd(inventory, v2, 4).run());
    assertEquals( 2, inventory.size() );
    assertTrue  ( Data.newClearCmd(inventory).run());
    assertEquals( 0, inventory.size() );
    assertTrue  ( UNDO.run() );
    assertEquals( 2, inventory.size() );
    assertTrue  ( REDO.run() );
    assertEquals( 0, inventory.size() );
  }

  public void test2() {
    final Inventory inventory = Data.newInventory();
    final Video v1 = Data.newVideo("K", 2003, "S");
    final RerunnableCommand UNDO = Data.newUndoCmd(inventory);
    final RerunnableCommand REDO = Data.newRedoCmd(inventory);
    assertTrue  ( Data.newAddCmd(inventory, v1,2).run());
    assertEquals( "K (2003) : S [2,0,0]", inventory.get(v1).toString() );
    assertTrue  ( Data.newOutCmd(inventory, v1).run());
    assertEquals( "K (2003) : S [2,1,1]", inventory.get(v1).toString() );
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,0,0]", inventory.get(v1).toString() );
    assertTrue  ( REDO.run() );
    assertEquals( "K (2003) : S [2,1,1]", inventory.get(v1).toString() );
    assertTrue  ( Data.newOutCmd(inventory, v1).run());
    assertEquals( "K (2003) : S [2,2,2]", inventory.get(v1).toString() );
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,1,1]", inventory.get(v1).toString() );
    assertTrue  ( UNDO.run() );
    assertEquals( "K (2003) : S [2,0,0]", inventory.get(v1).toString() );
  }
}
