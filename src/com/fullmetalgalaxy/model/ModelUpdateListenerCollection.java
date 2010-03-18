package com.fullmetalgalaxy.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Kroc
 * A helper class for implementers of the SourcesChangeEvents interface. This subclass of ArrayList assumes that
 * all objects added to it will be of type ModelUpdateListener.
 */
public class ModelUpdateListenerCollection extends ArrayList<ModelUpdateListener>
{
  static final long serialVersionUID = 1;

  public ModelUpdateListenerCollection()
  {
  }


  public void fireModelUpdate(SourceModelUpdateEvents p_sender)
  {
    ModelUpdateListener listener = null;
    for( Iterator<ModelUpdateListener> it = iterator(); it.hasNext(); )
    {
      listener = it.next();
      try
      {
        listener.onModelUpdate( p_sender );
      } catch( Exception e )
      {
        RpcUtil.logError( "the listener " + listener.getClass()
            + " bug while notify a model update", e );
      }
    }
  }

}
