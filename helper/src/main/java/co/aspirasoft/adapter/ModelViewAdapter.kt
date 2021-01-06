package co.aspirasoft.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.model.BaseModel
import co.aspirasoft.view.BaseView
import kotlin.reflect.KClass

open class ModelViewAdapter<T : BaseModel>(
    context: AppCompatActivity,
    private val models: List<T>,
    private val viewClass: KClass<out BaseView<T>>,
) : ArrayAdapter<BaseModel>(context, 0, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val model = models[position]
        var view = convertView

        if (view == null) {
            view = viewClass.constructors.elementAt(0).call(context)
            view.tag = view
        } else {
            view = view.tag as View
        }

        model.deleteObservers()
        (view as BaseView<T>).bindWithModel(model)
        model.notifyObservers()
        return view
    }

}