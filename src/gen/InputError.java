/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

/**
 *
 * @author danrothman
 */
public class InputError extends Throwable{
   private String friendlyError = "";
   
   public InputError(String friendlyError){
       super();
       this.friendlyError = friendlyError;
   }
   
   public String getFriendlyError(){
       return this.friendlyError;
   }
}
