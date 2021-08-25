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
 * Represents a single RGB color value (red, green, blue)
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CSSRGB
{
  private String m_sRed;
  private String m_sGreen;
  private String m_sBlue;

  /**
   * Copy constructor
   *
   * @param aOther
   *        The object to copy the data from. May not be <code>null</code>.
   */
  public CSSRGB (@Nonnull final CSSRGB aOther)
  {
    this (aOther.getRed (), aOther.getGreen (), aOther.getBlue ());
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
   */
  public CSSRGB (final int nRed, final int nGreen, final int nBlue)
  {
    this (Integer.toString (CSSColorHelper.getRGBValue (nRed)),
          Integer.toString (CSSColorHelper.getRGBValue (nGreen)),
          Integer.toString (CSSColorHelper.getRGBValue (nBlue)));
  }

  /**
   * Constructor
   *
   * @param sRed
   *        Red part.
   * @param sGreen
   *        Green part.
   * @param sBlue
   *        Blue part.
   */
  public CSSRGB (@Nonnull final String sRed,
                 @Nonnull final String sGreen,
                 @Nonnull final String sBlue)
  {
    setRed (sRed);
    setGreen (sGreen);
    setBlue (sBlue);
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
  public CSSRGB setRed (@Nonnull final String sRed)
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
  public CSSRGB setGreen (@Nonnull final String sGreen)
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
  public CSSRGB setBlue (@Nonnull final String sBlue)
  {
    m_sBlue = sBlue;
    return this;
  }

  /**
   * Convert this value to an RGBA value.
   *
   * @param fOpacity
   *        Opacity part. Is fitted to a value between 0 and 1.
   * @return This value as RGBA value with the passed opacity. Never
   *         <code>null</code>.
   * @since 3.8.3
   */
  @Nonnull
  public CSSRGBA getAsRGBA (final float fOpacity)
  {
    return new CSSRGBA(this, fOpacity);
  }

  /**
   * Convert this value to an RGBA value.
   *
   * @param sOpacity
   *        Opacity part. May neither be <code>null</code> nor empty.
   * @return This value as RGBA value with the passed opacity. Never
   *         <code>null</code>.
   * @since 3.8.3
   */
  @Nonnull
  public CSSRGBA getAsRGBA (@Nonnull final String sOpacity)
  {
    return new CSSRGBA(this, sOpacity);
  }

  /**
   * {@inheritDoc}
   *
   * @since 3.8.3
   */
  @Nonnull
  public String getAsString ()
  {
    return CCSSValue.PREFIX_RGB_OPEN + m_sRed + ',' + m_sGreen + ',' + m_sBlue + CCSSValue.SUFFIX_RGB_CLOSE;
  }

  /**
   * {@inheritDoc}
   *
   * @since 3.8.3
   */
  @Nonnull
  public CSSRGB getClone ()
  {
    return new CSSRGB (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final CSSRGB rhs = (CSSRGB) o;
    return m_sRed.equals (rhs.m_sRed) && m_sGreen.equals (rhs.m_sGreen) && m_sBlue.equals (rhs.m_sBlue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_sRed, m_sGreen, m_sBlue);
  }

  @Override
  public String toString() {
    return "CSSRGB{" +
            "m_sRed='" + m_sRed + '\'' +
            ", m_sGreen='" + m_sGreen + '\'' +
            ", m_sBlue='" + m_sBlue + '\'' +
            '}';
  }
}
