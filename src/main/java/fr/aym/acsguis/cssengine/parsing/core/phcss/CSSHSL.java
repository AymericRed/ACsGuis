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
 * Represents a single HSL color value (hue, saturation, lightness).
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CSSHSL
{
  private String m_sHue;
  private String m_sSaturation;
  private String m_sLightness;

  /**
   * Copy constructor
   *
   * @param aOther
   *        The object to copy the data from. May not be <code>null</code>.
   */
  public CSSHSL (@Nonnull final CSSHSL aOther)
  {
    this (aOther.getHue (), aOther.getSaturation (), aOther.getLightness ());
  }

  /**
   * Constructor
   *
   * @param nHue
   *        Hue value. Is scaled to the range 0-360
   * @param nSaturation
   *        Saturation value. Is cut to the range 0-100 (percentage)
   * @param nLightness
   *        Lightness value. Is cut to the range 0-100 (percentage)
   */
  public CSSHSL (final int nHue, final int nSaturation, final int nLightness)
  {
    this (Integer.toString (CSSColorHelper.getHSLHueValue (nHue)),
          Integer.toString (CSSColorHelper.getHSLPercentageValue (nSaturation)) + "%",
          Integer.toString (CSSColorHelper.getHSLPercentageValue (nLightness)) + "%");
  }

  /**
   * Constructor
   *
   * @param fHue
   *        Hue value. Is scaled to the range 0-360
   * @param fSaturation
   *        Saturation value. Is cut to the range 0-100 (percentage)
   * @param fLightness
   *        Lightness value. Is cut to the range 0-100 (percentage)
   */
  public CSSHSL (final float fHue, final float fSaturation, final float fLightness)
  {
    this (Float.toString (CSSColorHelper.getHSLHueValue (fHue)),
          Float.toString (CSSColorHelper.getHSLPercentageValue (fSaturation)) + "%",
          Float.toString (CSSColorHelper.getHSLPercentageValue (fLightness)) + "%");
  }

  public CSSHSL (@Nonnull final String sHue,
                 @Nonnull final String sSaturation,
                 @Nonnull final String sLightness)
  {
    setHue (sHue);
    setSaturation (sSaturation);
    setLightness (sLightness);
  }

  /**
   * @return hue part
   */
  @Nonnull
  public String getHue ()
  {
    return m_sHue;
  }

  @Nonnull
  public CSSHSL setHue (@Nonnull final String sHue)
  {
    m_sHue = sHue;
    return this;
  }

  /**
   * @return saturation part
   */
  @Nonnull
  public String getSaturation ()
  {
    return m_sSaturation;
  }

  @Nonnull
  public CSSHSL setSaturation (@Nonnull final String sSaturation)
  {
    m_sSaturation = sSaturation;
    return this;
  }

  /**
   * @return lightness part
   */
  @Nonnull
  public String getLightness ()
  {
    return m_sLightness;
  }

  @Nonnull
  public CSSHSL setLightness (@Nonnull final String sLightness)
  {
    m_sLightness = sLightness;
    return this;
  }

  /**
   * Convert this value to an HSLA value.
   *
   * @param fOpacity
   *        Opacity part. Is fitted to a value between 0 and 1.
   * @return This value as HSLA value with the passed opacity. Never
   *         <code>null</code>.
   * @since 3.8.3
   */
  @Nonnull
  public CSSHSLA getAsHSLA (final float fOpacity)
  {
    return new CSSHSLA(this, fOpacity);
  }

  /**
   * Convert this value to an HSLA value.
   *
   * @param sOpacity
   *        Opacity part. May neither be <code>null</code> nor empty.
   * @return This value as HSLA value with the passed opacity. Never
   *         <code>null</code>.
   * @since 3.8.3
   */
  @Nonnull
  public CSSHSLA getAsHSLA (@Nonnull final String sOpacity)
  {
    return new CSSHSLA(this, sOpacity);
  }

  /**
   * {@inheritDoc}
   *
   * @since 3.8.3
   */
  @Nonnull
  public String getAsString ()
  {
    return CCSSValue.PREFIX_HSL_OPEN + m_sHue + ',' + m_sSaturation + ',' + m_sLightness + CCSSValue.SUFFIX_HSL_CLOSE;
  }

  /**
   * {@inheritDoc}
   *
   * @since 3.8.3
   */
  @Nonnull
  public CSSHSL getClone ()
  {
    return new CSSHSL (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final CSSHSL rhs = (CSSHSL) o;
    return m_sHue.equals (rhs.m_sHue) &&
           m_sSaturation.equals (rhs.m_sSaturation) &&
           m_sLightness.equals (rhs.m_sLightness);
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_sHue, m_sSaturation, m_sLightness);
  }

  @Override
  public String toString() {
    return "CSSHSL{" +
            "m_sHue='" + m_sHue + '\'' +
            ", m_sSaturation='" + m_sSaturation + '\'' +
            ", m_sLightness='" + m_sLightness + '\'' +
            '}';
  }
}
