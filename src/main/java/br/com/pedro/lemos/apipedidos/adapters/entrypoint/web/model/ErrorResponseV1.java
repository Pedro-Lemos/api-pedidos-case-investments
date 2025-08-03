package br.com.pedro.lemos.apipedidos.adapters.entrypoint.web.model;

import java.util.Objects;

public class ErrorResponseV1 {

    private DataError data;

    public ErrorResponseV1() {

    }

    public ErrorResponseV1(DataError data) {
        this.data = data;
    }

    public DataError getData() {
        return data;
    }

    public void setData(DataError data) {
        this.data = data;
    }

    public static class DataError {
        private String codigoErro;
        private String motivoErro;

        public DataError() {
        }

        public DataError(String codigoErro, String motivoErro) {
            this.codigoErro = codigoErro;
            this.motivoErro = motivoErro;
        }

        public String getCodigoErro() {
            return codigoErro;
        }

        public void setCodigoErro(String codigoErro) {
            this.codigoErro = codigoErro;
        }

        public String getMotivoErro() {
            return motivoErro;
        }

        public void setMotivoErro(String motivoErro) {
            this.motivoErro = motivoErro;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            DataError dataError = (DataError) o;
            return Objects.equals(codigoErro, dataError.codigoErro) && Objects.equals(motivoErro, dataError.motivoErro);
        }

        @Override
        public int hashCode() {
            return Objects.hash(codigoErro, motivoErro);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponseV1 that = (ErrorResponseV1) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}
