/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.agent.MMbeanServer;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class CommandInterface
  extends StandardMBean
  implements CommandInterfaceMBean
{
  private static CommandInterface comInterface;
  
  public static synchronized CommandInterface getInstance()
  {
    Log.info("Registering Command Interface");
    if (comInterface == null) {
      try
      {
        comInterface = new CommandInterface();
      }
      catch (Exception ex)
      {
        Log.error("Critical error when init CommandInterface");
        throw new RuntimeException("Critical error when init CommandInterface!");
      }
    }
    return comInterface;
  }
  
  public CommandInterface()
    throws NotCompliantMBeanException, MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    super(CommandInterfaceMBean.class);
    MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=CommandInterface"));
  }
  
  public void callCommand(String args)
  {
    try
    {
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec(args);
      
      StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
      
      StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
      
      errorGobbler.start();
      outputGobbler.start();
      
      int exitVal = proc.waitFor();
      Log.info("ExitValue: " + exitVal);
    }
    catch (Throwable t)
    {
      Log.error("Error in execute remote command");
      Log.error(t);
    }
  }
}
