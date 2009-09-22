package org.basex.core;

import java.io.IOException;
import org.basex.io.PrintOutput;
import org.basex.query.QueryException;

/**
 * This wrapper executes commands locally.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class LocalSession implements Session {
  /** Database Context. */
  private final Context ctx;
  /** Process reference. */
  private Process proc;

  /**
   * Constructor.
   * @param context context
   */
  public LocalSession(final Context context) {
    ctx = context;
  }

  public boolean execute(final String str) throws IOException {
    try {
      return execute(new CommandParser(str, ctx).parse()[0]);
    } catch(final QueryException ex) {
      // [CG] strange, but there is no IOException(Throwable cause) on the Mac.
      throw new IOException(ex.getMessage());
    }
  }

  public boolean execute(final Process pr) {
    proc = pr;
    return pr.execute(ctx);
  }

  public void output(final PrintOutput out) throws IOException {
    proc.output(out);
  }

  public String info() {
    return proc.info();
  }

  public void close() {
  }
}