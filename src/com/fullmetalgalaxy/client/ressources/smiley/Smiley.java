package com.fullmetalgalaxy.client.ressources.smiley;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Smiley extends ClientBundle
{
  Smiley INSTANCE = GWT.create(Smiley.class);

  ImageResource bell();

  ImageResource cool();

  @Source("sad.png")
  ImageResource cry();

  ImageResource devil();

  ImageResource grimace();

  ImageResource heart();

  @Source("robot.png")
  ImageResource indifferent();

  ImageResource lol();

  ImageResource no();

  ImageResource poo();

  ImageResource robot();

  ImageResource rock();

  ImageResource sad();

  ImageResource skeptical();

  ImageResource smile();

  ImageResource tongue();

  ImageResource wink();

}
