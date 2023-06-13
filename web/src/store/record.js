

export default {
    state: {
        is_record: false,
        a_steps: "",
        b_steps: "",
        record_loser: "",
    },
    getters: {
    },
    mutations: {//调用用store.commit("函数名",)
        updateIsRecord(state, is_record) {
            state.is_record = is_record;
        },
        updateStep(state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
        updateRecordLoser(state, loser) {
            state.record_loser = loser;
        },
        
    },
    actions: {//调用用store.dispatch("函数名", )
        
        
        
    },
    modules: {
    }
}