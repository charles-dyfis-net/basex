package org.basex.query.func;

import static org.basex.query.QueryText.*;
import static org.basex.query.util.Err.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.logging.*;

import org.basex.io.serial.*;
import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.item.*;
import org.basex.util.*;

/**
 * Java function binding.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public final class JavaModuleFunc extends JavaMapping {
  /** Java module. */
  private final Object module;
  /** Method to be called. */
  private final Method mth;
  /** Log. */
  private static final Logger LOG = Logger.getLogger(JavaModuleFunc.class.getName());

  /**
   * Constructor.
   * @param ii input info
   * @param jm Java module
   * @param m Java method/field
   * @param a arguments
   */
  JavaModuleFunc(final InputInfo ii, final Object jm, final Method m, final Expr[] a) {
    super(ii, a);
    module = jm;
    mth = m;
  }

  @Override
  protected Object eval(final Value[] args, final QueryContext ctx)
      throws QueryException {

    // assign context if module is inheriting {@link QueryModule}
    if(module instanceof QueryModule) ((QueryModule) module).init(ctx);

    try {
      try {
        return mth.invoke(module, (Object[]) args);
      } catch(final IllegalArgumentException iae) {
        LOG.log(Level.FINEST,
            "Unable to call Java method {0} with arguments {1}; casting to Java types",
            new Object[] { mth, args });
        final Object[] ar = new Object[args.length];
        for(int a = 0; a < args.length; a++) ar[a] = args[a].toJava();
        return mth.invoke(module, ar);
      }
    } catch(final InvocationTargetException ex) {
      final Throwable cause = ex.getCause();
      LOG.log(Level.FINER, "Error calling Java module", cause);
      throw cause instanceof QueryException ? ((QueryException) cause).info(info) :
        JAVAERR.thrw(info, cause);
    } catch(final Throwable ex) {
      LOG.log(Level.FINER, "Error calling Java module", ex);
      // compose expected signature
      final TokenBuilder expect = new TokenBuilder();
      for(final Class<?> c : mth.getParameterTypes()) {
        if(!expect.isEmpty()) expect.add(", ");
        expect.add(c.getSimpleName());
      }
      throw JAVAMOD.thrw(info, mth.getName() + '(' + expect + ')',
          mth.getName() + '(' + foundArgs(args) + ')');
    }
  }

  @Override
  public void plan(final Serializer ser) throws IOException {
    ser.openElement(this, NAM, Token.token(name()));
    for(final Expr arg : expr) arg.plan(ser);
    ser.closeElement();
  }

  @Override
  public String description() {
    return name() + " method";
  }

  /**
   * Returns the function descriptor.
   * @return string
   */
  private String name() {
    return module.getClass().getSimpleName() + '.' + mth.getName();
  }

  @Override
  public String toString() {
    return name() + PAR1 + toString(SEP) + PAR2;
  }
}
