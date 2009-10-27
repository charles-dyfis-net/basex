package org.basex.core.proc;

import static org.basex.core.Text.*;
import org.basex.core.Commands.Cmd;
import org.basex.core.Commands.CmdCreate;
import org.basex.io.PrintOutput;

/**
 * Evaluates the 'alter user' command and alters the password of a user.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Andreas Weiler
 */
public final class AlterUser extends AAdmin {
  /**
   * Default constructor.
   * @param name user name
   * @param pw password
   */
  public AlterUser(final String name, final String pw) {
    super(name, pw);
  }

  @Override
  protected boolean exec(final PrintOutput out) {
    final String user = args[0];
    return context.users.alter(user, args[1]) ?
      info(USERALTER, user) : error(USERNO, user);
  }

  @Override
  public String toString() {
    return Cmd.CREATE + " " + CmdCreate.USER + args();
  }
}