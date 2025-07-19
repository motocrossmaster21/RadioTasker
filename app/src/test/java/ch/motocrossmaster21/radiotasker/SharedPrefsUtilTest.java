package ch.motocrossmaster21.radiotasker;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class SharedPrefsUtilTest {
    @Mock
    Context context;
    @Mock
    SharedPreferences prefs;
    @Mock
    SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(prefs);
        Mockito.when(prefs.edit()).thenReturn(editor);
        Mockito.when(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor);
    }

    @Test
    public void testDefaultValues() {
        Mockito.when(prefs.getString("deviceName", "VW BT 6485")).thenReturn("VW BT 6485");
        Mockito.when(prefs.getString("packageName", "radioenergy.app")).thenReturn("radioenergy.app");
        assertEquals("VW BT 6485", SharedPrefsUtil.getDeviceName(context));
        assertEquals("radioenergy.app", SharedPrefsUtil.getPackageName(context));
    }

    @Test
    public void testSetValues() {
        SharedPrefsUtil.setDeviceName(context, "TestDevice");
        SharedPrefsUtil.setPackageName(context, "test.app");
        Mockito.verify(editor).putString("deviceName", "TestDevice");
        Mockito.verify(editor).putString("packageName", "test.app");
        Mockito.verify(editor, Mockito.times(2)).apply();
    }
}
