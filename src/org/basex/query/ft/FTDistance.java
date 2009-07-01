package org.basex.query.ft;

import static org.basex.util.Token.*;
import java.io.IOException;
import org.basex.data.FTMatch;
import org.basex.data.FTStringMatch;
import org.basex.data.Serializer;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.QueryTokens;
import org.basex.query.expr.Expr;
import org.basex.util.Tokenizer;

/**
 * FTDistance expression.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Christian Gruen
 */
public final class FTDistance extends FTFilter {
  /** Distance. */
  private final Expr[] dist;

  /**
   * Constructor.
   * @param e expression
   * @param d distances
   * @param u unit
   */
  public FTDistance(final FTExpr e, final Expr[] d, final FTUnit u) {
    super(e);
    dist = d;
    unit = u;
  }

  @Override
  protected boolean filter(final QueryContext ctx, final FTMatch mtc,
      final Tokenizer ft) throws QueryException {

    final long min = checkItr(dist[0], ctx);
    final long max = checkItr(dist[1], ctx);
    mtc.sort();

    final FTMatch match = new FTMatch();
    FTStringMatch sm = null;
    FTStringMatch f = null;
    for(final FTStringMatch m : mtc) {
      if(m.not) {
        match.add(m);
      } else {
        if(sm != null) {
          final int d = pos(m.start, ft) - pos(sm.end, ft) - 1;
          if(d < min || d > max) return false;
        } else {
          f = m;
        }
        sm = m;
      }
    }
    f.end = sm.end;
    mtc.reset();
    mtc.add(f);
    mtc.add(match);
    return true;
  }

  @Override
  public void plan(final Serializer ser) throws IOException {
    ser.openElement(this, token(QueryTokens.DISTANCE),
        token(dist[0] + "-" + dist[1] + " " + unit));
    super.plan(ser);
  }

  @Override
  public String toString() {
    return super.toString() + QueryTokens.DISTANCE + "(" +
      dist[0] + "-" + dist[1] + " " + unit + ")";
  }
}