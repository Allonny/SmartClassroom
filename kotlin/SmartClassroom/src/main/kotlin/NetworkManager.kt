class NetworkManager(val dataBus: DataBus) {

    init {
        dataBus.networkManager = this
    }
}