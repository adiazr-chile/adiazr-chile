/**
 * Corregir problema progressmeter cuando está oculto
 */
zul.wgt.Progressmeter.prototype._fixImgWidth = _zkf = function() {
	var n = this.$n(), 
		img = this.$n('img');
	if (img) {
		if (zk(n).isRealVisible()) 
			var $img = jq(img);
			if ($img) {
				$img.animate({
					width: Math.round((jq(n).innerWidth() * this._value) / 100) + 'px'
				}, $img.zk.getAnimationSpeed('slow'));
			}
	}
	
	/**
	 * No recuerdo
	 */
	zk.afterLoad(function () {
		function _isListgroup(w) {
			return zk.isLoaded('zkex.sel') && w.$instanceof(zkex.sel.Listgroup);
		}
		function _isListgroupfoot(w) {
			return zk.isLoaded('zkex.sel') && w.$instanceof(zkex.sel.Listgroupfoot);
		}
		zk.override(zul.sel.SelectWidget.prototype, '_isAllSelected', function () {
			if (!this._selItems.length)
				return false;
			var isGroupSelect = this.groupSelect;
			for (var it = this.getBodyWidgetIterator({skipHidden:true}), w; (w = it.next());) {
				if (!w.isCheckable() || !w._loaded) // 2534 workaround: skip unloaded items
			    	continue;
				if ((_isListgroup(w) || _isListgroupfoot(w)) && !isGroupSelect)
					continue;
				if (!w.isDisabled() && !w.isSelected())
					return false;
			}
			return true;
		});
		
		zk.override(zul.sel.SelectWidget.prototype, 'selectAll', function (notify, evt) {
			for (var it = this.getBodyWidgetIterator(), w; (w = it.next());)
				if (!w.isDisabled() && w.isCheckable())
					this._changeSelect(w, true);
			if (notify && evt !== true)
				this.fireOnSelect(this.getSelectedItem(), evt);
		});
	});
}