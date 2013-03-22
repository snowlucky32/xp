package com.enonic.wem.api.content.schema.mixin;

import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class MixinTest
{

    @Test
    public void adding_a_formItemSetMixin_to_another_formItemSetMixin_throws_exception()
    {
        ModuleName module = ModuleName.from( "myModule" );

        Mixin ageMixin = newMixin().module( module ).formItem( newInput().name( "age" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        final FormItemSet personFormItemSet = newFormItemSet().name( "person" ).addFormItem(
            newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newMixinReference( ageMixin ).name( "age" ).build() ).build();
        Mixin personMixin = newMixin().module( module ).formItem( personFormItemSet ).build();

        Mixin addressMixin = newMixin().module( module ).formItem( newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personFormItemSet.add( newMixinReference( addressMixin ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A Mixin cannot reference other Mixins unless it is of type InputMixin: FormItemSetMixin", e.getMessage() );
        }
    }

    @Test
    public void tags()
    {
        ModuleName module = ModuleName.from( "myModule" );
        Input input = newInput().name( "tags" ).label( "Tags" ).inputType( InputTypes.TEXT_LINE ).multiple( true ).build();
        Mixin inputMixin = Mixin.newMixin().module( module ).formItem( input ).build();
    }

}
