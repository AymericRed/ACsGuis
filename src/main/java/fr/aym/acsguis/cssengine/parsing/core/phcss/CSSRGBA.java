/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.aym.acsguis.cssengine.parsing.core.phcss;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Represents a single RGBA color value (red, green, blue, opacity)
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CSSRGBA
{
  private String m_sRed;
  private String m_sGreen;
  private String m_sBlue;
  private String m_sOpacity;

  /**
   * Copy constructor
   *
   * @param aOther
   *        The object to copy the data from. May not be <code>null</code>.
   */
  public CSSRGBA (@Nonnull final CSSRGBA aOther)
  {
    this (aOther.getRed (), aOther.getGreen (), aOther.getBlue (), aOther.getOpacity ());
  }

  /**
   * Constructor
   *
   * @param aOther
   *        The RGB value to use as the basis. May not be <code>null</code>.
   * @param fOpacity
   *        Opacity part. Is fitted to a value between 0 and 1.
   * @since 3.8.3
   */
  public CSSRGBA (@Nonnull final CSSRGB aOther, final float fOpacity)
  {
    this (aOther, Float.toString (CSSColorHelper.getOpacityToUse (fOpacity)));
  }

  /**
   * Constructor
   *
   * @param aOther
   *        The RGB value to use as the basis. May not be <code>null</code>.
   * @param sOpacity
   *        Opacity part. May neither be <code>null</code> nor empty.
   * @since 3.8.3
   */
  public CSSRGBA (@Nonnull final CSSRGB aOther, @Nonnull final String sOpacity)
  {
    this (aOther.getRed (), aOther.getGreen (), aOther.getBlue (), sOpacity);
  }

  /**
   * Constructor
   *
   * @param nRed
   *        Red part. Is fitted to a value between 0 and 255.
   * @param nGreen
   *        Green part. Is fitted to a value between 0 and 255.
   * @param nBlue
   *        Blue part. Is fitted to a value between 0 and 255.
   * @param fOpacity
   *        Opacity part. Is fitted to a value between 0 and 1.
   */
  public CSSRGBA (final int nRed, final int nGreen, final int nBlue, final float fOpacity)
  {
    this (Integer.toString (CSSColorHelper.getRGBValue (nRed)),
          Integer.toString (CSSColorHelper.getRGBValue (nGreen)),
          Integer.toString (CSSColorHelper.getRGBValue (nBlue)),
          Float.toString (CSSColorHelper.getOpacityToUse (fOpacity)));
  }

  /**
   * Constructor
   *
   * @param sRed
   *        Red part. May neither be <code>null</code> nor empty.
   * @param sGreen
   *        Green part. May neither be <code>null</code> nor empty.
   * @param sBlue
   *        Blue part. May neither be <code>null</code> nor empty.
   * @param sOpacity
   *        Opacity part. May neither be <code>null</code> nor empty.
   */
  public CSSRGBA (@Nonnull final String sRed,
                  @Nonnull final String sGreen,
                  @Nonnull final String sBlue,
                  @Nonnull final String sOpacity)
  {
    setRed (sRed);
    setGreen (sGreen);
    setBlue (sBlue);
    setOpacity (sOpacity);
  }

  /**
   * @return red part
   */
  @Nonnull
  public String getRed ()
  {
    return m_sRed;
  }

  @Nonnull
  public CSSRGBA setRed (@Nonnull final String sRed)
  {
    m_sRed = sRed;
    return this;
  }

  /**
   * @return green part
   */
  @Nonnull
  public String getGreen ()
  {
    return m_sGreen;
  }

  @Nonnull
  public CSSRGBA setGreen (@Nonnull final String sGreen)
  {
    m_sGreen = sGreen;
    return this;
  }

  /**
   * @return blue part
   */
  @Nonnull
  public String getBlue ()
  {
    return m_sBlue;
  }

  @Nonnull
  public CSSRGBA setBlue (@Nonnull final String sBlue)
  {
    m_sBlue = sBlue;
    return this;
  }

  /**
   * @return opacity part
   */
  @Nonnull
  public String getOpacity ()
  {
    return m_sOpacity;
  }

  @Nonnull
  public CSSRGBA setOpacity (@Nonnull final String sOpacity)
  {
    m_sOpacity = sOpacity;
    return this;
  }

  /**
   * @return This value as RGB value without the opacity. Never
   *         <code>null</code>.
   * @since 3.8.3
   */
  @Nonnull
  public CSSRGB getAsRGB ()
  {
    return new CSSRGB(m_sRed, m_sGreen, m_sBlue);
  }

  /**
   * {@inheritDoc}
   *
   * @since 3.8.3
   */
  @Nonnull
  public String getAsString ()
  {
    return CCSSValue.PREFIX_RGBA_OPEN +
           m_sRed +
           ',' +
           m_sGreen +
           ',' +
           m_sBlue +
           ',' +
           m_sOpacity +
           CCSSValue.SUFFIX_RGBA_CLOSE;
  }

  /**
   * {@inheritDoc}
   *
   * @since 3.8.3
   */
  @Nonnull
  public CSSRGBA getClone ()
  {
    return new CSSRGBA (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final CSSRGBA rhs = (CSSRGBA) o;
    return m_sRed.equals (rhs.m_sRed) &&
           m_sGreen.equals (rhs.m_sGreen) &&
           m_sBlue.equals (rhs.m_sBlue) &&
           m_sOpacity.equals (rhs.m_sOpacity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_sRed, m_sGreen, m_sBlue, m_sOpacity);
  }

  @Override
  public String toString() {
    return "CSSRGBA{" +
            "m_sRed='" + m_sRed + '\'' +
            ", m_sGreen='" + m_sGreen + '\'' +
            ", m_sBlue='" + m_sBlue + '\'' +
            ", m_sOpacity='" + m_sOpacity + '\'' +
            '}';
  }
}
